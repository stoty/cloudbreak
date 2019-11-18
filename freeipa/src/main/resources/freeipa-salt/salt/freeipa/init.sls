freeipa-install:
  pkg.installed:
    - pkgs:
        - ntp
        - ipa-server
        - ipa-server-dns

net.ipv6.conf.lo.disable_ipv6:
  sysctl.present:
    - value: 0

{% for host in salt['pillar.get']('freeipa:hosts') %}
/etc/hosts/{{ host['fqdn'] }}:
  host.present:
    - ip:
      - {{ host['ip'] }}
    - names:
      - {{ host['fqdn'] }}
{% endfor %}

/opt/salt/scripts/freeipa_install.sh:
  file.managed:
    - makedirs: True
    - user: root
    - group: root
    - mode: 700
    - source: salt://freeipa/scripts/freeipa_install.sh

{% if salt['pillar.get']('freeipa:mode') == 'FIRST_INSTALL' %}
install-freeipa:
  cmd.run:
    - name: /opt/salt/scripts/freeipa_install.sh "FIRST_INSTALL" && echo $(date +%Y-%m-%d:%H:%M:%S) >> /var/log/freeipa_install-executed
    - env:
        - FPW: {{salt['pillar.get']('freeipa:password')}}
        - DOMAIN: {{salt['pillar.get']('freeipa:domain')}}
        - REALM: {{salt['pillar.get']('freeipa:realm')}}
        - ADMIN_USER: {{salt['pillar.get']('freeipa:admin_user')}}
        - FREEIPA_TO_REPLICATE: {{salt['pillar.get']('freeipa:freeipa_to_replicate')}}
    - unless: test -f /var/log/freeipa_install-executed
    - require:
        - file: /opt/salt/scripts/freeipa_install.sh

{% elif salt['pillar.get']('freeipa:mode') == 'REPLICA_INSTALL' %}
install-freeipa-replica:
  cmd.run:
    - name: /opt/salt/scripts/freeipa_install.sh "REPLICA_INSTALL" && echo $(date +%Y-%m-%d:%H:%M:%S) >> /var/log/freeipa_replica_install-executed
    - env:
        - FPW: {{salt['pillar.get']('freeipa:password')}}
        - DOMAIN: {{salt['pillar.get']('freeipa:domain')}}
        - REALM: {{salt['pillar.get']('freeipa:realm')}}
        - ADMIN_USER: {{salt['pillar.get']('freeipa:admin_user')}}
        - FREEIPA_TO_REPLICATE: {{salt['pillar.get']('freeipa:freeipa_to_replicate')}}
    - unless: test -f /var/log/freeipa_replica_install-executed
    - require:
        - file: /opt/salt/scripts/freeipa_install.sh

{% endif %}

/usr/lib/python2.7/site-packages/ipaserver/plugins/getkeytab.py:
  file.managed:
    - makedirs: True
    - user: root
    - group: root
    - mode: 644
    - source: salt://freeipa/scripts/getkeytab.py

restart_freeipa_after_plugin_change:
  service.running:
    - name: ipa
    - onlyif: test -f /etc/ipa/default.conf
    - watch:
      - file: /usr/lib/python2.7/site-packages/ipaserver/plugins/getkeytab.py

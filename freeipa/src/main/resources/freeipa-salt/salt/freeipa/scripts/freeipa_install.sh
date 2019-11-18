#!/usr/bin/env bash

set -e

function setCaConfiguration() {
    PROPERTY=$1
    VALUE=$2
    ESCAPED_PROPERTY=${PROPERTY//./[.]}
    TMP_FILE=`mktemp`
    cat /var/lib/pki/pki-tomcat/ca/conf/CS.cfg | sed "s/$ESCAPED_PROPERTY=.*/$PROPERTY=$VALUE/g" > $TMP_FILE
    cat $TMP_FILE > /var/lib/pki/pki-tomcat/ca/conf/CS.cfg
    rm -f $TMP_FILE
    systemctl restart pki-tomcatd@pki-tomcat
}


MODE=$1
FQDN=$(hostname -f)
IPADDR=$(hostname -i)

if [ "$FQDN" == "$FREEIPA_TO_REPLICATE" -a "$MODE" == "FIRST_INSTALL" ]; then
    ipa-server-install \
          --realm $REALM \
          --domain $DOMAIN \
          --hostname $FQDN \
          -a $FPW \
          -p $FPW \
          --setup-dns \
          --auto-reverse \
          --allow-zone-overlap \
          --ssh-trust-dns \
          --mkhomedir \
          --ip-address $IPADDR \
          --auto-forwarders \
          --unattended

    # Fix for https://pagure.io/freeipa/issue/7872
    setCaConfiguration ca.crl.MasterCRL.nextUpdateGracePeriod 60
fi

if [ "$FQDN" != "$FREEIPA_TO_REPLICATE" -a "$MODE" == "REPLICA_INSTALL" ]; then
    ipa-replica-install \
          --server $FREEIPA_TO_REPLICATE \
          --setup-ca \
          --realm $REALM \
          --domain $DOMAIN \
          --hostname $FQDN \
          --principal $ADMIN_USER \
          --admin-password $FPW \
          --setup-dns \
          --auto-reverse \
          --allow-zone-overlap \
          --ssh-trust-dns \
          --mkhomedir \
          --ip-address $IPADDR \
          --auto-forwarders \
          --unattended

    # Fix for https://pagure.io/freeipa/issue/7872
    setCaConfiguration ca.crl.MasterCRL.nextUpdateGracePeriod 60
fi

set +e

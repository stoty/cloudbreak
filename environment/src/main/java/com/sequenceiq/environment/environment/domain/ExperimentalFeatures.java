package com.sequenceiq.environment.environment.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.environment.api.v1.environment.model.base.IdBrokerMappingSource;
import com.sequenceiq.environment.api.v1.environment.model.base.Tunnel;

@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentalFeatures implements Serializable {

    @Enumerated(EnumType.STRING)
    private Tunnel tunnel;

    @Enumerated(EnumType.STRING)
    @Column(name = "idbroker_mapping_source")
    private IdBrokerMappingSource idBrokerMappingSource;

    @JsonInclude(Include.NON_NULL)
    @Transient
    private Boolean shortClusterNames;

    @JsonInclude(Include.NON_NULL)
    @Transient
    private Boolean createNiFiMachineUser;

    public Tunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public IdBrokerMappingSource getIdBrokerMappingSource() {
        return idBrokerMappingSource;
    }

    public void setIdBrokerMappingSource(IdBrokerMappingSource idBrokerMappingSource) {
        this.idBrokerMappingSource = idBrokerMappingSource;
    }

    public Boolean getShortClusterNames() {
        return shortClusterNames;
    }

    public void setShortClusterNames(Boolean shortClusterNames) {
        this.shortClusterNames = shortClusterNames;
    }

    public Boolean getCreateNiFiMachineUser() {
        return createNiFiMachineUser;
    }

    public void setCreateNiFiMachineUser(Boolean createNiFiMachineUser) {
        if (createNiFiMachineUser != null && createNiFiMachineUser) {
            shortClusterNames = true;
        }
        this.createNiFiMachineUser = createNiFiMachineUser;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return tunnel == null && idBrokerMappingSource == null && shortClusterNames == null && createNiFiMachineUser == null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Tunnel tunnel;

        private IdBrokerMappingSource idBrokerMappingSource;

        private Boolean shortClusterNames;

        private Boolean createNiFiMachineUser;

        private Builder() {
        }

        public Builder withTunnel(Tunnel tunnel) {
            this.tunnel = tunnel;
            return this;
        }

        public Builder withIdBrokerMappingSource(IdBrokerMappingSource idBrokerMappingSource) {
            this.idBrokerMappingSource = idBrokerMappingSource;
            return this;
        }

        public Builder withShortClusterNames(Boolean shortClusterNames) {
            this.shortClusterNames = shortClusterNames;
            return this;
        }

        public Builder withCreateNiFiMachineUser(Boolean createNiFiMachineUser) {
            this.createNiFiMachineUser = createNiFiMachineUser;
            return this;
        }

        public ExperimentalFeatures build() {
            ExperimentalFeatures experimentalFeatures = new ExperimentalFeatures();
            experimentalFeatures.setTunnel(tunnel);
            experimentalFeatures.setIdBrokerMappingSource(idBrokerMappingSource);
            // the order is important for the 2 flag setters below
            experimentalFeatures.setShortClusterNames(shortClusterNames);
            experimentalFeatures.setCreateNiFiMachineUser(createNiFiMachineUser);
            return experimentalFeatures;
        }
    }
}

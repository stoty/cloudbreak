package com.sequenceiq.cloudbreak.cm.polling.task;

import com.sequenceiq.cloudbreak.cm.ClouderaManagerOperationFailedException;
import com.sequenceiq.cloudbreak.cm.client.ClouderaManagerApiPojoFactory;
import com.sequenceiq.cloudbreak.cm.polling.ClouderaManagerPollerObject;

public class ClouderaManagerKerberosConfigureListenerTask extends AbstractClouderaManagerCommandCheckerTask<ClouderaManagerPollerObject> {

    public ClouderaManagerKerberosConfigureListenerTask(ClouderaManagerApiPojoFactory clouderaManagerApiPojoFactory) {
        super(clouderaManagerApiPojoFactory);
    }

    @Override
    public void handleTimeout(ClouderaManagerPollerObject toolsResourceApi) {
        throw new ClouderaManagerOperationFailedException("Operation timed out. Failed to configure kerberos Cloudera Manager services.");
    }

    @Override
    public String successMessage(ClouderaManagerPollerObject toolsResourceApi) {
        return "Cloudera Manager succesfully configure Kerberos.";
    }

    @Override
    protected String getCommandName() {
        return "Configure for kerberos";
    }
}

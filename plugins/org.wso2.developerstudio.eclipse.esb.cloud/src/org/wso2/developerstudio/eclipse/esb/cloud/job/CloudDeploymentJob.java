/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.developerstudio.eclipse.esb.cloud.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.esb.cloud.Activator;
import org.wso2.developerstudio.eclipse.esb.cloud.client.IntegrationCloudServiceClient;
import org.wso2.developerstudio.eclipse.esb.cloud.exceptions.CloudDeploymentException;
import org.wso2.developerstudio.eclipse.esb.cloud.exceptions.InvalidTokenException;
import org.wso2.developerstudio.eclipse.esb.cloud.model.Application;
import org.wso2.developerstudio.eclipse.esb.cloud.model.EndpointData;
import org.wso2.developerstudio.eclipse.esb.cloud.model.Version;
import org.wso2.developerstudio.eclipse.esb.cloud.resources.CloudDeploymentWizardConstants;
import org.wso2.developerstudio.eclipse.esb.cloud.util.JsonUtils;
import org.wso2.developerstudio.eclipse.esb.cloud.wizard.DeploymentStatusDialog;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

/**
 * Represents a background job to generate the Docker image.
 *
 */
public class CloudDeploymentJob extends Job {

    private String name;
    private String description;
    private String version;
    private String carbonFileName;
    private String carbonFileLocation;
    private String iconLocation;
    private List<Map<String, String>> tags;
    private boolean isNewVersion;
    private IntegrationCloudServiceClient client;
    private String response;
    private EndpointData endpointData;

    private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

    public CloudDeploymentJob(String name, String description, String version, String carbonFileName, String carbonFileLocation, String iconLocation, List<Map<String, String>> tags, boolean isNewVersion) {
        super("Deploying to Integration Cloud...");
        client = IntegrationCloudServiceClient.getInstance();
        this.name = name;
        this.description = description;
        this.version = version;
        this.carbonFileName = carbonFileName;
        this.carbonFileLocation = carbonFileLocation;
        this.iconLocation = iconLocation;
        this.tags = tags;
        this.isNewVersion = isNewVersion;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        String operationText = "Preparing to deploy the application... ";
        monitor.beginTask(operationText, 100);

        operationText = "Sending application data...";
        monitor.subTask(operationText);
        monitor.worked(10);

        try {
            
            showMessageBox(CloudDeploymentWizardConstants.DIALOG_TITLE_TEXT,
                    CloudDeploymentWizardConstants.SuccessMessages.SUCCESS_CREATING_APPLICATION_MSG,
                    SWT.ICON_INFORMATION);

            client.createApplication(name, description, version, carbonFileName, carbonFileLocation, iconLocation, tags, isNewVersion);
            
            operationText = "Fetching the endpoints...";
            monitor.subTask(operationText);
            monitor.worked(10);
            
            ScheduledExecutorService scheduledExecutorService =
                    Executors.newScheduledThreadPool(5);
            
            Application app = client.getApplication(name);

            ScheduledFuture<?> scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    int count = 0;
                    
                    public void run(){
                        try {
                            Map<String, Version> versions = app.getVersions();
                            
                            for (Map.Entry<String, Version> version : versions.entrySet()) {
                                response = client.getApplicationEndpoints(app.getApplicationType(), version.getValue().getDeploymentURL(), version.getValue().getVersionId());
                                if (null != response &&  !"null".equals(response)) {
                                    endpointData = JsonUtils.getEndpointDataFromJson(response);
                                    scheduledExecutorService.shutdown();
                                } else if (count < 15){
                                    monitor.subTask("Loading Endpoints...");
                                    monitor.worked(5);
                                }
                            }
                        } catch(CloudDeploymentException | InvalidTokenException e) {
                            log.error(CloudDeploymentWizardConstants.ErrorMessages.DEPLOY_TO_CLOUD_FAILED_MESSAGE, e);
                            showMessageBox(CloudDeploymentWizardConstants.ErrorMessages.DEPLOY_TO_CLOUD_FAILED_TITLE,
                                    e.getMessage(), SWT.ICON_ERROR);
                            scheduledExecutorService.shutdown();
                        }
                    }
                },
                0,
                5,
                TimeUnit.SECONDS);

            scheduledExecutorService.awaitTermination(120, TimeUnit.SECONDS);

        } catch (CloudDeploymentException | InvalidTokenException | InterruptedException e) {
            log.error(CloudDeploymentWizardConstants.ErrorMessages.DEPLOY_TO_CLOUD_FAILED_MESSAGE, e);
            showMessageBox(CloudDeploymentWizardConstants.ErrorMessages.DEPLOY_TO_CLOUD_FAILED_TITLE, e.getMessage(), SWT.ICON_ERROR);
            operationText = e.getMessage();
            monitor.beginTask(operationText, 100);
            monitor.worked(0);
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }

        monitor.worked(100);
        monitor.done();
        
        showDeploymentSuccessPopup();

//        showMessageBox(CloudDeploymentWizardConstants.SuccessMessages.SUCCESSFUL_TITLE,
//                CloudDeploymentWizardConstants.SuccessMessages.DEPLOY_TO_CLOUD_SUCCESS_MESSAGE,
//                SWT.ICON_INFORMATION);

        return Status.OK_STATUS;
    }
    
    /**
     * Show deployment success window with endpoints
     * 
     */
    private void showDeploymentSuccessPopup(){
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                Display display = PlatformUI.getWorkbench().getDisplay();
                Shell shell = display.getActiveShell();
                
                DeploymentStatusDialog dialog = new DeploymentStatusDialog(shell, endpointData);
                dialog.open();

//                MessageNotificationPopup pop = new MessageNotificationPopup(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), shell);
//                ArrayList<String> links = new ArrayList<>();
//                links.add("Test Link");
//                System.out.println(endpointData.getSoapEndpoints());
////                links.addAll(endpointData.getSoapEndpoints().stream().map(Endpoint::getWsdl).collect(Collectors.toList()));
//                pop.setContent("Deployment status", "Your application has been successfully deployed to WSO2 Intgeration Cloud. \n Try it out using the below endpoints.", links);
//                pop.open();
            }
        });
    }

    /**
     * Show message box
     * 
     * @param title
     * @param message
     * @param style
     */
    private void showMessageBox(String title, String message, int style) {

        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                Display display = PlatformUI.getWorkbench().getDisplay();
                Shell shell = display.getActiveShell();

                MessageBox exportMsg = new MessageBox(shell, style);
                exportMsg.setText(title);
                exportMsg.setMessage(message);

                exportMsg.open();
            }
        });
    }


}

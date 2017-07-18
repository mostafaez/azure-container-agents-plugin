package com.microsoft.azure.containeragents;

import com.microsoft.azure.containeragents.helper.AzureContainerServiceCredentials;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.naming.AuthenticationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by xianyu on 7/14/2017.
 */
public class KubernetesClientFactory {
    public static KubernetesClient buildWithConfigFile(final String configContent) throws IOException {
        File tempKubeConfigFile = File.createTempFile("kube", ".config", new File(System.getProperty("java.io.tmpdir")));
        tempKubeConfigFile.deleteOnExit();

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempKubeConfigFile));
        bufferedWriter.write(configContent);
        bufferedWriter.close();

        System.setProperty(Config.KUBERNETES_KUBECONFIG_FILE, tempKubeConfigFile.getPath());
        return new DefaultKubernetesClient(Config.autoConfigure());
    }

    public static KubernetesClient buildWithKeyPair(final String url,
                                                    final String namespace,
                                                    final AzureContainerServiceCredentials.KubernetesCredential acsCredentials) {
        ConfigBuilder builder = new ConfigBuilder();
        builder.withMasterUrl(url)
                .withCaCertData(acsCredentials.getServerCertificate())
                .withNamespace(namespace)
                .withClientCertData(acsCredentials.getClientCertificate())
                .withClientKeyData(acsCredentials.getClientKey())
                .withWebsocketPingInterval(0);
        return new DefaultKubernetesClient(builder.build());
    }
}
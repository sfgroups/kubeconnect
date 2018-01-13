package kube

import io.kubernetes.client.ApiClient
import io.kubernetes.client.Configuration
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.models.V1PodList
import io.kubernetes.client.util.Config
import io.kubernetes.client.util.KubeConfig

class ListPods {
    def static main(args) {
        println("Starting ListPods")

        CliBuilder cli = new CliBuilder(usage: 'ListPods')
        cli.c(args: 1, argName: 'config', "Kubernetes configurtion file", required: false)
        def options = cli.parse(args)

        String inputFile = ""
        if( options.c){ }
        if(options.c ){ inputFile=options.c?.trim()}
        KubeConfig kube

        if (inputFile?.length()>2) {
            println("Using Config file :" + inputFile)
            kube = KubeConfig.loadKubeConfig(new FileReader(inputFile))
        } else {
            inputFile= System.getenv("USERPROFILE") +File.separator +".kube" + File.separator + "config"
            if( new File(inputFile).exists()){
                println("Using Config file :" + inputFile)
                kube = KubeConfig.loadKubeConfig(new FileReader(inputFile))
            } else {
                if (!System.getenv("HOME")) {
                    println("HOME environemnt varible missing. Aborting..")
                    System.exit(1)
                }
                kube = KubeConfig.loadDefaultKubeConfig();
            }
        }
        ApiClient client = Config.fromConfig(kube);
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api()
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            println(item.getMetadata().getName())
        }
    }

    def ListPods(){
        def pods = api.listNamespacedPod("default", null, null, null, null, null, null)
        println(pods)
        return
    }
}
package sfg

import com.google.common.io.ByteStreams
import io.kubernetes.client.ApiClient
import io.kubernetes.client.Configuration
import io.kubernetes.client.Exec
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.models.V1PodList
import io.kubernetes.client.util.Config
import io.kubernetes.client.util.KubeConfig


/**
 * Created by rishi on 11/30/2017.
 */
class ListPods {

    def static main(args){
        println("PODS")
        if( !System.getenv("HOME")){
            println("HOME environemnt varible missing. Aborting..")
            System.exit(1)

        }

        KubeConfig kube= KubeConfig.loadDefaultKubeConfig();
        ApiClient client=Config.fromConfig(kube);
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        Exec(api)
        return
        def pods=api.listNamespacedPod("default",null, null, null, null, null, null)
        println(pods)
        return

        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            println(item)
            System.out.println(item.getMetadata().getName());
        }
    }

 /*   def static Logs(CoreV1Api api){
        PodLogs logs = new PodLogs();
        V1Pod pod = api.listNamespacedPod("default", "false", null, null, null, null, null, null, null, null).getItems().get(0);

        InputStream is = logs.streamNamespacedPodLog(pod);
        ByteStreams.copy(is, System.out);
    }*/
def static Exec(CoreV1Api api){


    Exec exec = new Exec();
    boolean tty = System.console() != null;
    String[] command =["sh", "-c", "echo foo"]
     final Process proc = exec.exec("default", "net-tools-79676cb96f-rc57k",command, true, tty);
  //  final Process proc = exec.exec("default", "net-tools-79676cb96f-rc57k",  {"sh"}, true, tty);


    new Thread(new Runnable() {
        public void run() {
            try {
                ByteStreams.copy(System.in, proc.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }).start();

    new Thread(new Runnable() {
        public void run() {
            try {
                ByteStreams.copy(proc.getInputStream(), System.out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }).start();

    proc.waitFor();
    try {
        // Wait for buffers to flush.
        Thread.sleep(2000);
    } catch (InterruptedException ex) {
        ex.printStackTrace();
    }

    proc.destroy();
    System.exit(0);
}
}

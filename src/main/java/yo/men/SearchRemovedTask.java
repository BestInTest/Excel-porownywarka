package yo.men;

import lukfor.progress.TaskService;
import lukfor.progress.tasks.ITaskRunnable;
import lukfor.progress.tasks.monitors.ITaskMonitor;

import java.util.ArrayList;
import java.util.Collection;

public class SearchRemovedTask implements Runnable {
    @Override
    public void run() {
        Collection<Main.Data> diff = new ArrayList<>();

        ITaskRunnable task = new ITaskRunnable() {

            @Override
            public void run(ITaskMonitor monitor) {

                long max = Math.max(data1List.size(), data2List.size());
                monitor.begin("Szukanie usunietych danych", max);

                for (Main.Data data1 : data1List) {
                    String addr1 = data1.getCellAddr().formatAsString();
                    boolean removed = true;
                    for (Main.Data data2 : data2List) {
                        String addr2 = data2.getCellAddr().formatAsString();
                        if (data1.getSheetIndex() == data2.getSheetIndex()) {
                            if (addr1.equals(addr2)) {
                                //diff.add(data2.getCellAddr());
                                removed = false;
                                break;
                            }
                        }
                    }
                    if (removed) {
                        diff.add(data1);
                    }
                    monitor.worked(1);
                }

                monitor.done();

            }
        };
        TaskService.run(task);

        return diff;
    }
}

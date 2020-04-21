package net.tccn.qtask;

import net.tccn.base.Kv;
import net.tccn.base.MetaKit;
import org.redkale.convert.json.JsonConvert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by liangxianyou at 2019/4/20 19:59.
 */
public class TaskKit {
    static final JsonConvert convert = JsonConvert.root();
    private static List<TaskEntity> taskEntities;

    static {
        init();
    }

    public static void init() {
        taskEntities = MetaKit.getTaskEntities();
    }

    public static Task buildTask(String name, String platToken, Kv para) {
        TaskEntity taskEntity = getTaskEntity(name, platToken);
        return buildTask(taskEntity, para);
    }

    public static Task buildTask(TaskEntity taskEntity, Kv para) {
        Task task = new Task();
        task.setName(taskEntity.getName());
        task.setTitle(taskEntity.getTitle());
        task.setContent(taskEntity.getContent());
        task.setDbPlatId(taskEntity.getDbPlatId());
        task.setCatalog(taskEntity.getCatalog());

        Kv _para = Kv.of().putAll(para);
        if (taskEntity.getPara() != null) {
            try {
                Map<String, String> map = convert.convertFrom(JsonConvert.TYPE_MAP_STRING_STRING, taskEntity.getPara());
                map.forEach((k,v) -> _para.put(k, v));
            } catch (Exception e) {
                new IllegalArgumentException(String.format("fromJson error:[%s]",taskEntity.getPara()), e);
            }
        }
        task.setPara(_para);
        task.setDbAccount(MetaKit.getDbPlat(taskEntity.getDbPlatId()));
        return task;
    }

    public static TaskEntity getTaskEntity(String name, String platToken) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(platToken);

        Optional<TaskEntity> any = taskEntities.stream()
                .filter(x -> name.equals(x.getName()) && MetaKit.getPlatId(platToken).equals(x.getSysPlatId()))
                .findAny();
        return any.get();
    }

    public static Object taskRun(String name, String platToken, Kv para) {
        Task task = buildTask(name, platToken, para);
        return QRuner.query(task);
    }

    public static Object taskRun(TaskEntity entity) {
        Task task = buildTask(entity, Kv.of());
        return QRuner.query(task);
    }
}

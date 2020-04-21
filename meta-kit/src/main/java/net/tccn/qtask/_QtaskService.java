package net.tccn.qtask;

import net.tccn.base.BaseService;
import net.tccn.base.JBean;
import net.tccn.base.MetaKit;
import net.tccn.base.PageBean;
import org.redkale.net.http.RestParam;
import org.redkale.net.http.RestService;
import org.redkale.source.Flipper;
import org.redkale.util.Comment;

@RestService(automapping = true)
public class _QtaskService extends BaseService {

    @Comment("qtask列表")
    public JBean list(TaskEntity task, Flipper flipper, @RestParam(name = "platToken") String token) {
        if (task == null) {
            task = new TaskEntity();
        }
        task.setSysPlatId(platId(token));
        PageBean<TaskEntity> page = TaskEntity.dao.findPage(task, flipper);


        return JBean.by(0, "", page);
    }

    @Comment("qtask保存")
    public JBean save(TaskEntity task, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();

        do {
            if (isEmpty(task.getName())) {
                jBean.set(-1, "任务标识码不能为空");
                break;
            }

            // 同平台name 唯一校验
            TaskEntity bean = new TaskEntity();
            bean.setSysPlatId(platId(token));
            bean.setName(task.getName());

            TaskEntity entity = TaskEntity.dao.findFirst(bean);
            if (entity != null && !entity.getKey().equals(task.getKey())) {
                jBean.set(-1, "任务标识码被占用");
                break;
            }

            if (task.getKey() != null) {
                task.update();
            } else {
                task.setSysPlatId(platId(token));
                task.save();
            }
            MetaKit.reload(task);
        } while (false);



        return jBean;
    }

    @Comment("debug调试接口")
    public JBean debug(TaskEntity task, @RestParam(name = "platToken") String token) {
        JBean jBean = new JBean();

        Object res = TaskKit.taskRun(task);
        jBean.setBody(res);
        return jBean;
    }

}

package net.tccn.qtask;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;

/**
 * Created by liangxianyou at 2019/4/20 20:04.
 */
@Data
@Table(name = "qtask", catalog = "db_demo")
public class TaskEntity extends Doc<TaskEntity> {
    public static TaskEntity dao = dao(TaskEntity.class);

    private String name;        // 任务标识码
    private String title;       // 任务名称
    //private String cate;      // 任务类型
    private String dbPlatId;    // 数据平台id
    private String catalog;     //

    private String content;     // 任务内容
    private String remark;      // 任务备注
    private String para;        // 任务参数
    private String sysPlatId;   // 平台id

    // ---------------------
}

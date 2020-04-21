package net.tccn.qtask;

import lombok.Data;
import net.tccn.base.Kv;
import net.tccn.base.dbq.jdbc.api.DbAccount;

/**
 * |- dbp: 调用谁， 参数，
 * |- 谁：谁（干什么） => dbPlatId + content    （在程序的世界每个个体，往往都有其明确的职责）
 * |-
 * 任务对象
 */
@Data
public class Task {

    private String name;        // 任务标识，同一系统唯一
    private String dbPlatId;    // 数据源id
    private String catalog;     // 数据库 database

    private String content;     // 任务内容
    private String title;       // 任务标题
    private Kv para;            // 任务参数
    private String remark;      // 任务备注

    private DbAccount dbAccount;

}

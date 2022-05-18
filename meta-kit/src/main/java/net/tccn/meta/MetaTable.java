package net.tccn.meta;

import lombok.Data;
import net.tccn.base.arango.Doc;
import net.tccn.base.dbq.table.Field;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 元数据
 *
 * @author: liangxianyou at 2018/10/17 12:58.
 */
@Data
@Table(name = "MetaTable", catalog = "db_demo")
public class MetaTable extends Doc<MetaTable> implements Serializable {
    public static final MetaTable dao = MetaTable.dao(MetaTable.class);

    private String name;
    private String alias; //表别名：全库唯一，程序自动生成
    private String comment;
    private List<Field> items;
    private String sysPlatId; //所属系统平台
    private String dbPlatId; //所属数据平台
    private String catalog; //所在database

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Field> getItems() {
        return items;
    }

    public void setItems(List<Field> items) {
        this.items = items;
    }

    public String getSysPlatId() {
        return sysPlatId;
    }

    public void setSysPlatId(String sysPlatId) {
        this.sysPlatId = sysPlatId;
    }

    public String getDbPlatId() {
        return dbPlatId;
    }

    public void setDbPlatId(String dbPlatId) {
        this.dbPlatId = dbPlatId;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Integer getHv() {
        return hv;
    }

    public void setHv(Integer hv) {
        this.hv = hv;
    }

    private Integer hv;//临时
    // ------------------------------------------------
    public static MetaTable toAs(net.tccn.base.dbq.table.Table table) {
        List<Field> fields = table.getColumns().stream().map(Field::toAs).collect(Collectors.toList());

        MetaTable _bean = new MetaTable();
        _bean.setName(table.getName());
        _bean.setComment(table.getComment());
        _bean.setCatalog(table.getCatalog());
        _bean.setItems(fields);

        return _bean;
    }

    // 方法名getPK 报错，
    public String[] pk() {
        List<String> pks = items.stream().filter(x -> x.getPk() != null && x.getPk()).map(x -> x.getName()).collect(Collectors.toList());

        if (pks.size() > 0) {
            return pks.toArray(new String[pks.size()]);
        }

        //存在id字段，取id
        Optional<Field> any = items.stream().filter(x -> x.getName().equalsIgnoreCase("id")).findAny();
        return any.isPresent() ? new String[]{"id"} : new String[0];
    }
}

package net.tccn.plat;

import lombok.Data;
import net.tccn.base.arango.Doc;

import javax.persistence.Table;

/**
 * @author: liangxianyou at 2018/11/26 17:46.
 */
@Data
@Table(name = "sys_plat", catalog = "db_dev")
public class SysPlat extends Doc<SysPlat> {
    public static SysPlat dao = dao(SysPlat.class);

    private String name;
    private String token;

    public SysPlat() {
    }

    public SysPlat(String token) {
        this.token = token;
    }

}

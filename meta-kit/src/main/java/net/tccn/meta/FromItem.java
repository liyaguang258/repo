package net.tccn.meta;

import lombok.Data;

import java.util.List;

/**
 * @author: liangxianyou
 */
@Data
public class FromItem {
    private String col;
    private String label;
    private String inType;
    private String InExt;
    private List<String> cks;

}

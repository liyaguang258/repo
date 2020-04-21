package net.tccn.meta;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: liangxianyou
 */
@Data
public class Filter {
    private String name;
    private String label;
    private boolean checked;
    private String inType;
    private String inExt;
    private List<Map<String, String>> filterType;
}

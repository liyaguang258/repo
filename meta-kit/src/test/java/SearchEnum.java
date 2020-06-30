import org.redkale.util.TypeToken;

public enum SearchEnum {


    VISLOG("vislog", "vislog.find", "vislog.update", new TypeToken<SearchResult<SearchVisLogRecord>>() {
    });

    public final String esIndex;
    public final String findTplName;
    public final String updateTplName;
    public final TypeToken typeToken;

    SearchEnum(String esIndex, String findTplName, String updateTplName, TypeToken typeToken) {
        this.esIndex = esIndex;
        this.findTplName = findTplName;
        this.updateTplName = updateTplName;
        this.typeToken = typeToken;
    }
}

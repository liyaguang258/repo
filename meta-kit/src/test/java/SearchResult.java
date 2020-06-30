import lombok.Getter;
import lombok.Setter;
import org.redkale.util.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchResult<T> {
    private int took;
    private boolean timed_out;
    private Shards _shards;
    private Hits<T> hits;

    @Getter
    @Setter
    public static class HitRecord<T> {
        private String _index;
        private String _type;
        private String _id;
        private float _score;
        private T _source;
    }

    @Getter
    @Setter
    public static class Hits<T> {
        private RecordTotal total;
        private float max_score;
        private ArrayList<HitRecord<T>> hits;
    }

    @Getter
    @Setter
    public static class RecordTotal {
        private int value;
        private String relation;
    }

    @Getter
    @Setter
    public static class Shards {
        private int total;
        private int successful;
        private int skipped;
        private int failed;
    }

    public Sheet<T> getSheet() {
        int total = 0;
        List<T> list = new ArrayList<>();
        if (hits != null) total = hits.getTotal().getValue();
        if (total > 0) list = hits.getHits().stream().map(x -> x.get_source()).collect(Collectors.toList());

        return new Sheet<>(total, list);
    }
}

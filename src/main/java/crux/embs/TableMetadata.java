package crux.embs;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

public class TableMetadata {

    private final String tableName;

    private final boolean canReplace;

    private final List<String> pks;

    public TableMetadata(String tableName, boolean canReplace, List<String> pks) {
        Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName is nul/empty");
        if(!canReplace) {
            Preconditions.checkArgument(CollectionUtils.isEmpty(pks), "pks is null/empty. pks must be specified if canReplace=false.");
        }

        this.tableName = tableName;
        this.canReplace = canReplace;
        this.pks = pks == null ? Lists.newArrayList() : pks;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isCanReplace() {
        return canReplace;
    }

    public List<String> getPks() {
        return pks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableMetadata that = (TableMetadata) o;

        return tableName.equals(that.tableName);
    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }
}

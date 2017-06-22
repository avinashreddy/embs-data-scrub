package crux.embs;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class RunConfig {

    private String runtTimeUTC;

    @PostConstruct
    public synchronized void init() {
        Preconditions.checkState(runtTimeUTC == null,
                "runtTimeUTC is set. Can only be set once. value is %s", runtTimeUTC);
        runtTimeUTC = ZonedDateTime.now(ZoneOffset.UTC).toString();
    }

    public synchronized String getRuntTimeUTC() {
        return runtTimeUTC;
    }
}

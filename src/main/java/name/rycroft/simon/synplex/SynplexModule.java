package name.rycroft.simon.synplex;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class SynplexModule extends AbstractModule {

    private final String[] args;

    public SynplexModule(String[] args) {
        this.args = args;
    }

    @Provides
    @Singleton
    public String[] provideArgs() {
        return args;
    }

    @Override
    protected void configure() {
        bind(ListAccounts.class);
        bind(Arguments.class);
    }
}

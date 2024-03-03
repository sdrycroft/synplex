package name.rycroft.simon.synplex;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import java.util.List;

import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

@Singleton
public class Arguments {

    private static final String LIST_ACCOUNTS = "listAccounts";
    private static final String ACCOUNTS = "accounts";
    private static final String DATABASE_FILE_PATH = "databaseFilePath";
    private static final String WAIT_TIME_IN_SECONDS = "waitTimeInSeconds";
    private static final String SINGLE_RUN = "singleRun";
    private final String[] args;
    private final ArgumentParser argumentParser;

    @Inject
    public Arguments(String[] args) {
        this.args = args;

        argumentParser = ArgumentParsers.newFor("Synplex")
                .build()
                .description("""
                        Synchronise Plex viewing.
                                        
                        Simple application that will synchronise the viewing stats between a list of
                        accounts and the main admin account. Originally created as a way of keeping the
                        admin account (mum and dad) and a single managed account (girl and boy) in sync.
                        """);

        String defaultDatabaseFilename = "/var/lib/plexmediaserver/Library/Application Support/Plex Media Server/Plug-in Support/Databases/com.plexapp.plugins.library.db";
        argumentParser.addArgument("-d", "--database-file")
                .setDefault(defaultDatabaseFilename)
                .metavar("PATH")
                .help("Path to the 'com.plexapp.plugins.library.db' database file. Defaults to '%s'".formatted(defaultDatabaseFilename))
                .dest(DATABASE_FILE_PATH);

        ArgumentGroup listAccountsGroup = argumentParser.addArgumentGroup("List accounts");

        listAccountsGroup.addArgument("-l", "--list-accounts")
                .help("List the accounts that can be copied. Used for getting a list of IDs for the main process.")
                .action(storeTrue())
                .dest(LIST_ACCOUNTS);

        ArgumentGroup synchroniseAccountsGroup = argumentParser.addArgumentGroup("Synchronise accounts");

        synchroniseAccountsGroup.addArgument("-a", "--accounts")
                .help("List of accounts to sync with the main admin account.")
                .metavar("ID")
                .type(Long.class)
                .nargs("+")
                .help("IDs of the accounts to sync.")
                .dest(ACCOUNTS);

        synchroniseAccountsGroup.addArgument("-s", "--single-run")
                .help("Whether the application should exit after the first run, or run continuously.")
                .dest(SINGLE_RUN)
                .action(storeTrue());

        Long defaultWaitSeconds = 300L;
        synchroniseAccountsGroup.addArgument("-w", "--wait")
                .type(Long.class)
                .metavar("SECONDS")
                .setDefault(defaultWaitSeconds)
                .help("Time to wait in seconds between each synchronisation. Defaults to %s seconds.".formatted(defaultWaitSeconds))
                .dest(WAIT_TIME_IN_SECONDS);
    }

    public Long waitTimeInSeconds() throws ArgumentParserException {
        return argumentParser.parseArgs(args).getLong(WAIT_TIME_IN_SECONDS);
    }

    public String databaseFilePath() throws ArgumentParserException {
        return argumentParser.parseArgs(args).getString(DATABASE_FILE_PATH);
    }

    public boolean listAccounts() throws ArgumentParserException {
        return argumentParser.parseArgs(args).getBoolean(LIST_ACCOUNTS);
    }

    public List<Long> accounts() throws ArgumentParserException {
        List<Long> accounts = argumentParser.parseArgs(args).getList(ACCOUNTS);
        if (accounts.contains(1L)) {
            throw new RuntimeException("1 is not a supported account ID and should not be included in the list of accounts to sync.");
        }
        return accounts;
    }

    public boolean singleRun() throws ArgumentParserException {
        return argumentParser.parseArgs(args).getBoolean(SINGLE_RUN);
    }
}

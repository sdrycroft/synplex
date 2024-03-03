package name.rycroft.simon;

import com.google.inject.Guice;
import com.google.inject.Injector;
import name.rycroft.simon.synplex.Arguments;
import name.rycroft.simon.synplex.ListAccounts;
import name.rycroft.simon.synplex.SyncViews;
import name.rycroft.simon.synplex.SynplexModule;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;

import static java.lang.Thread.sleep;

public class Synplex {

    private static final Logger logger = LoggerFactory.getLogger(Synplex.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SynplexModule(args));
        Arguments arguments = injector.getInstance(Arguments.class);
        try {
            if (arguments.listAccounts()) {
                System.out.println("Accounts");
                ListAccounts listAccounts = injector.getInstance(ListAccounts.class);
                listAccounts.listAccounts()
                        .forEach(((name, id) -> System.out.format("%20s : %12d\n", name, id)));
            } else {
                do {
                    SyncViews syncViews = injector.getInstance(SyncViews.class);
                    arguments.accounts().forEach(syncViews::sync);
                } while (!arguments.singleRun() && sleepBetweenRuns(arguments));
            }
        } catch (HelpScreenException ignored) {
        } catch (RuntimeException exception) {
            logger.error(exception.getMessage());
        } catch (ArgumentParserException ignored) {
            logger.error("Invalid arguments supplied.");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            logger.error("Unable to execute query.");
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            logger.error("Interrupted exception.");
        }
    }

    private static boolean sleepBetweenRuns(Arguments arguments) throws ArgumentParserException, InterruptedException {
        sleep(Duration.ofSeconds(arguments.waitTimeInSeconds()));
        return true;
    }
}
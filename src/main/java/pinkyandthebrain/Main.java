package pinkyandthebrain;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Properties;

public class Main {

    public static final Logger log = LoggerFactory.getLogger("app");

    @SuppressWarnings("unchecked")
    public static void main(String... args) throws Exception {

        Preconditions.checkArgument(args.length >= 1, "Usage: Main busy_day.in redundancy.in");

        boolean isSaveToFile = "true".equals(System.getProperty("save"));
        String baseDirName = StringUtils.defaultIfBlank(System.getProperty("d"), "judge");
        File baseDir = null;

        if (isSaveToFile) {
            baseDir = new File(baseDirName);
            FileUtils.forceMkdir(baseDir);
        }

        int score = 0;

        Properties conf = new Properties();
        File confFile = new File("simulation.properties");
        if (confFile.exists()) {
            conf.load(new FileReader(confFile));
        }

        for (String arg : args) {
            String playerClassName = (String) conf.getOrDefault("player", "pinkyandthebrain.players.DummyPlayer");
            Class<Player> playerClass = (Class<Player>) Class.forName(playerClassName);
            Player player = playerClass.newInstance();
            log.info("Loading configuration for player " + playerClassName);
            Simulation simulation = new Loader(player).loadFromResource(arg);

            log.info("Loaded simulation parameters from classpath resource {}", arg);

            simulation.start();

            log.info("Simulation {} scored: {}", arg, simulation.getScore());

            if (isSaveToFile) {
                File commandsFile = new File(baseDir, arg + ".txt");
                PrintWriter out = new PrintWriter(new FileOutputStream(commandsFile));
                simulation.printCommands(out);
                out.flush();
                out.close();
                log.info("Written commands to {}", commandsFile);
            }

            score += simulation.getScore();
        }

        log.info("Total score: {}", score);
    }
}

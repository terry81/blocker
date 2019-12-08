package com.blocker.blocker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IPLogger {
    Logger logger = LoggerFactory.getLogger(BlockerApplication.class);

    @Value("${blocker.log_filename}")
    private String logFileName;

    @Value("${blocker.whitelisted_ips}")
    private String[] whitelistedIPs;

    public void log(String ip) throws IOException {

        boolean isIPWhiteListed = Arrays.stream(whitelistedIPs).anyMatch(ip::equals);

        if (isIPWhiteListed) {
            logger.error(ip + " is whitelisted. Won't be logged.\n");
            return;
        }

        if (new File(logFileName).exists()) {
            try (Stream<String> stream = Files.lines(Paths.get(logFileName), StandardCharsets.UTF_8)) {
                if (stream.anyMatch(ip::equals)) {
                    logger.error(ip + " is already logged. Won't be logged.\n");
                    return;
                }
            }
        } else {
            new File(logFileName).createNewFile();
        }

        logger.error(ip + " is NOT whitelisted and will be logged.\n");
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true));
        writer.write(ip);
        writer.newLine();
        writer.close();

    }
}

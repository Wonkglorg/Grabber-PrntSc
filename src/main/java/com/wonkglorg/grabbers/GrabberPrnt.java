package com.wonkglorg.grabbers;

import com.wonkglorg.util.web.DownloadResult;
import com.wonkglorg.util.web.Downloader;
import com.wonkglorg.util.web.Status;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wonkglorg.util.console.ConsoleUtil.printr;

public class GrabberPrnt extends Downloader {

    protected Logger LOGGER = Logger.getLogger(GrabberPrnt.class.getName());
    private Thread downloaderThread;
    private String currentUrl;
    private Path outputPath;
    private final String webUrl = "https://prnt.sc";
    private int delay = 50;

    /**
     * Create a new instance of the GrabberPrnt class
     *
     * @param outputPath     The output path to save the files to
     * @param currentUrlPath The current url path to start from should be a format like "6znlyp"
     * @param delay          The delay between each request
     */
    public GrabberPrnt(Path outputPath, String currentUrlPath, int delay) {
        this.outputPath = outputPath;
        this.currentUrl = currentUrlPath;
        this.delay = delay;
    }

    public GrabberPrnt(Path outputPath, String currentUrl) {
        this(outputPath, currentUrl, 20);
    }

    public Thread start(boolean extraInfo) {
        File file = outputPath.toFile();

        if (!file.exists()) {
            file.mkdirs();
        }

        downloaderThread = new Thread(() -> {
            long start = System.currentTimeMillis();
            while (true) {
                DownloadResult result = processUrl(currentUrl, extraInfo);
                incrementStatus(result.status());

                currentUrl = increment(currentUrl);


                printr(formattedProgress((System.currentTimeMillis() - start) / 1000, currentUrl));

                try {
                    Thread.sleep(delay);  // Use Thread.sleep instead of wait
                } catch (InterruptedException e) {
                    if (extraInfo) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                    throw new RuntimeException(e);
                }
            }
        });

        downloaderThread.start();
        return downloaderThread;
    }


    /**
     * Process the given url
     *
     * @param currentUrl   The current url
     * @param extraInfo    Extra info
     * @return The download result
     */
    private DownloadResult processUrl(String currentUrl, boolean extraInfo) {
        try {
            //get website elements

            Document doc = getDocument(webUrl + "/" + currentUrl, extraInfo);
            if (doc == null) {
                return new DownloadResult(null, null, Status.ERROR, "Failed to get document");
            }

            Element imgElement = doc.select("img.no-click.screenshot-image").first();

            if (imgElement != null) {
                return downloadFile(imgElement.attr("abs:src"),outputPath.resolve(currentUrl + ".png").toFile(), extraInfo);
            }

            return new DownloadResult(null, null, Status.ERROR, "No image element found");

        } catch (Exception e) {
            if (extraInfo) {
                LOGGER.log(Level.INFO, "Error getting element reference falling back to default: " + e.getMessage(), e);
            }
            return new DownloadResult(null, null, Status.ERROR, "Error getting element reference");
        }
    }

    /**
     * Increment the given value to the next valid url value
     *
     * @param value The current value
     * @return The next value
     */
    private String increment(String value) {
        char[] chars = value.toCharArray();

        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == 'z') {
                chars[i] = '0';
            } else if (chars[i] == '9') {
                chars[i] = 'a';
                break;
            } else {
                chars[i]++;
                break;
            }
        }
        return new String(chars);
    }


}

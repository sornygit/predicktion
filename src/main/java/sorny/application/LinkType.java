package sorny.application;

/**
 * Created by Magnus on 2016-12-06.
 */
public class LinkType {
    public static boolean isImageLink(String url) {
        return url.endsWith(".gif") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg");
    }

    public static boolean isVideoLink(String url) {
        return url.endsWith(".mov") || url.endsWith(".vid") || url.endsWith(".mp4") || url.endsWith(".mpeg4") ||
                url.endsWith(".avi");
    }

    public static boolean isYouTubeLink(String url) {
        return url.contains("youtube.com");
    }

    public static boolean isOtherLink(String url) {
        return !isImageLink(url) && !isVideoLink(url) && !isYouTubeLink(url);
    }
}

package AS4;

public class FaceDetectionThread implements Runnable {
    private BedtimeBot bedtimeBot;

    public FaceDetectionThread(BedtimeBot bedtimeBot) {
        this.bedtimeBot = bedtimeBot;
    }

    @Override
    public void run() {
        while (true) {
            if (bedtimeBot.isUserAsleep()) {
                bedtimeBot.setSleeping(true);
                break;
            }

            try {
                Thread.sleep(500);  // Check every 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

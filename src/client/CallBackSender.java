package client;

/**
 * Created by Виктория on 05.06.2017.
 */
public class CallBackSender implements CallBack {
    private boolean isSent = false;
    @Override
    public void onConfirm() {
        this.isSent = true;
    }

    @Override
    public boolean isConfirmed() {
        return this.isSent;
    }

}

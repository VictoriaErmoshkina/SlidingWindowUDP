package client;

/**
 * Created by Виктория on 05.06.2017.
 */
public class CallBackReader implements CallBack {
    private boolean isRead = false;
    @Override
    public void onConfirm() {
            this.isRead = true;
    }
    @Override
    public boolean isConfirmed(){
        return this.isRead;
    }

}

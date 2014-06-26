package cn.kli.t9search.framework.app;

public interface IAppLoadListener {
    public static class Result{
        int code;
        String info;
    }
    
    void onProgressUpdate(float progress, float max, String info);
    void onFinished();
}

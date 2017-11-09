//package lava.bluepay.com.lavaapp.sevice;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.support.annotation.NonNull;
//
///**
// * Created by bluepay on 2017/11/7.
// */
//
//public class PrefsHelper {
//    private static SharedPreferences sharedPreferences;
//    private static PrefsHelper prefsInstance;
//    private static final String DEFAULT_STRING_VALUE = "";
//
//    private PrefsHelper(@NonNull Context context, String preferencesName){
//        sharedPreferences = context.getApplicationContext().getSharedPreferences(
//                preferencesName,
//                Context.MODE_PRIVATE
//        );
//    }
//
//    /**
//     * 根据context和SharedPreferences文件名返回SharedPreferences实例
//     * @param context 上下文
//     * @param preferencesName SharedPreferences文件名
//     *
//     * @return SharedPreferences实例
//     */
//    public static PrefsHelper with(@NonNull Context context,String preferencesName) {
//        prefsInstance = new PrefsHelper(context,preferencesName);
//        return prefsInstance;
//    }
//
//    //region ======================================== String related methods ========================================
//
//    /**
//     * @param what
//     * @return Returns the stored value of 'what'
//     */
//    public String read(String what) {
//        return sharedPreferences.getString(what, DEFAULT_STRING_VALUE);
//    }
//
//    /**
//     * @param what
//     * @param defaultString
//     * @return Returns the stored value of 'what'
//     */
//    public String read(String what, String defaultString) {
//        return sharedPreferences.getString(what, defaultString);
//    }
//
//    /**
//     * @param where
//     * @param what
//     */
//    public void write(String where, String what) {
//        sharedPreferences.edit().putString(where, what).apply();
//    }
//
//    //endregion ======================================== String related methods ========================================
//}

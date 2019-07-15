package com.hdyl.bluetoothinfo.utils.bufferknife;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hdyl.bluetoothinfo.utils.log.impl.LogUitls;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * <p>Created by liugd on 2018/4/4.<p>
 * <p>佛祖保佑，永无BUG<p>
 */

public class MyBufferKnifeUtils {


//    @MyBindView(R.id.text)
//    private TextView textView;

    public static void inject(final Object obj, View rootView) {
        Field error = null;
        HashMap<Integer, View> map = new HashMap<>();

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                MyBindView bindView = field.getAnnotation(MyBindView.class);
                if (bindView != null) {
                    error = field;
                    field.setAccessible(true);
                    View var = rootView.findViewById(bindView.value());
                    map.put(bindView.value(), var);
                    field.set(obj, var);
                    //废弃掉
                    boolean isClick = bindView.click();
                    if (isClick && obj instanceof View.OnClickListener) {
                        var.setOnClickListener((View.OnClickListener) obj);
                    }
                }
            }
            final Method method = obj.getClass().getDeclaredMethod("onClick", View.class);
            MyOnClick onClick = method.getAnnotation(MyOnClick.class);
            if (onClick != null) {
                for (int value : onClick.value()) {

                    View view = map.get(value);
                    if (view == null) {
                        view = rootView.findViewById(value);
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            method.setAccessible(true);
                            try {
                                method.invoke(obj, v);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (NoSuchMethodException e1) {
//            e1.printStackTrace();
        } catch (Exception e) {

            LogUitls.print(TAG, "error field " + error);
            RuntimeException runtimeException = new RuntimeException("注解异常", e);
            throw runtimeException;
        }
    }

    private static final String TAG = "MyBufferKnifeUtils";


    public static void injetClick(Object obj, View rootView) {
//        try {
//            Method[] methods = obj.getClass().getDeclaredMethods();
//            for (Method method : methods) {
//                MyOnClick onClick = method.getAnnotation(MyOnClick.class);
//                if (onClick != null) {
//                    for (int value : onClick.value()) {
//                        rootView.findViewById(value).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                method.setAccessible(true);
//                                try {
//                                    method.invoke(obj, v);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        } catch (Exception e) {
//            RuntimeException runtimeException = new RuntimeException("注解异常");
//            runtimeException.initCause(e);
//            throw runtimeException;
//        }

        //      Method method= obj.getClass().getDeclaredMethod("onClick",View.class);
    }

    public static void inject(Activity activity) {
        inject(activity, activity.getWindow().getDecorView());
    }



    public static void inject(Fragment fragment) {
        inject(fragment, fragment.getView());
    }

}

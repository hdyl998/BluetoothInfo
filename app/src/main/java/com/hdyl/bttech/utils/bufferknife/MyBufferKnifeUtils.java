package com.hdyl.bttech.utils.bufferknife;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;


import com.hdyl.bttech.utils.log.impl.LogUitls;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>Created by liugd on 2018/4/4.<p>
 * <p>佛祖保佑，永无BUG<p>
 */

public class MyBufferKnifeUtils {


    /***
     * 注入方法
     * @param obj
     * @param rootView
     */
    public static void inject(Object obj, View rootView) {
        inject(obj, rootView, null);
    }

    public static void inject(Activity activity, Class injectClazz) {
        inject(activity, activity.getWindow().getDecorView(),injectClazz);
    }
    /***
     * 注入方法
     * @param obj
     * @param rootView
     * @param injectClazz 反射的class
     */
    public static void inject(final Object obj, View rootView, Class injectClazz) {
        Field error = null;

        Class classInject;
        if (injectClazz != null) {
            classInject = injectClazz;
        } else {
            classInject = obj.getClass();
        }


        SparseArray<View> map = new SparseArray<>();
        try {
            Field[] fields = classInject.getDeclaredFields();
            for (Field field : fields) {
                MyBindView bindView = field.getAnnotation(MyBindView.class);
                if (bindView != null) {
                    error = field;
                    field.setAccessible(true);
                    View var = rootView.findViewById(bindView.value());
                    map.put(bindView.value(), var);
                    field.set(obj, var);
                    //废弃掉,但还可以继续使用,以后会删除
                    boolean isClick = bindView.click();
                    if (isClick && obj instanceof View.OnClickListener) {
                        var.setOnClickListener((View.OnClickListener) obj);
                    }
                }
            }
            final Method method = classInject.getDeclaredMethod("onClick", View.class);
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
            //忽略
//            e1.printStackTrace();
        } catch (Exception e) {
            LogUitls.print(TAG, "error field " + error);
            throw new RuntimeException("注解异常", e);
        }
//        calcTime.printResult(TAG + classInject.getSimpleName());
    }

    private static final String TAG = "MyBufferKnifeUtils";


    public static void inject(Activity activity) {
        inject(activity, activity.getWindow().getDecorView());
    }

//    public static void inject(IBaseDialog dialog) {
//        inject(dialog, dialog.getViewRoot());
//    }

    public static void inject(View view) {
        inject(view, view);
    }

    public static void inject(Fragment fragment) {
        inject(fragment, fragment.getView());
    }

}

package com.hdyl.bttech.utils.loopdo;

import java.util.ArrayList;
import java.util.List;

/**
 * Note：None
 * Created by Liuguodong on 2019/2/19 14:13
 * E-Mail Address：986850427@qq.com
 */
public class LifeCircleManager implements ILifeCircle {

    private List<ILifeCircle> list = new ArrayList<>(3);


    public void addElement(ILifeCircle element) {
        if (element != null)
            list.add(element);
    }

    public void removeElement(ILifeCircle element) {
        list.remove(element);
    }


    @Override
    public void onPause() {
        for (ILifeCircle circle : list) {
            circle.onPause();
        }
    }

    @Override
    public void onResume() {
        for (ILifeCircle circle : list) {
            circle.onResume();
        }
    }

    @Override
    public void onDestory() {
        for (ILifeCircle circle : list) {
            circle.onDestory();
        }
    }
}

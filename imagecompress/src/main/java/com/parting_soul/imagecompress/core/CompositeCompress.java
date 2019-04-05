package com.parting_soul.imagecompress.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author parting_soul
 * @date 2019/4/5
 */
public class CompositeCompress {
    public List<IImageCompress> mCompresses;

    public CompositeCompress() {
        this.mCompresses = new ArrayList<>();
    }

    public void add(IImageCompress compress) {
        this.mCompresses.add(compress);
    }

    public void destroy() {
        for (IImageCompress compress : mCompresses) {
            if (compress != null) {
                compress.destroy();
            }
        }
    }

}

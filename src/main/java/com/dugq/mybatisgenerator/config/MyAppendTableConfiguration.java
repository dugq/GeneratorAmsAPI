package com.dugq.mybatisgenerator.config;

import com.dugq.mybatisgenerator.context.MyContext;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;

/**
 * @author dugq
 * @date 2022/7/7 10:54 上午
 */
public class MyAppendTableConfiguration extends TableConfiguration {
    private Context context;
    /**
     * Instantiates a new table configuration.
     *
     * @param context the context
     */
    public MyAppendTableConfiguration(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean areAnyStatementsEnabled() {
        if (context instanceof MyContext){
            return true;
        }
        return super.areAnyStatementsEnabled();
    }
}

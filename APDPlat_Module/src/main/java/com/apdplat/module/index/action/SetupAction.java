/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apdplat.module.index.action;

import com.apdplat.module.index.model.IndexScheduleConfig;
import com.apdplat.module.index.service.IndexSchedulerService;
import com.apdplat.platform.action.ExtJSActionSupport;
import com.apdplat.platform.search.IndexManager;
import com.apdplat.platform.util.Struts2Utils;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Scope("prototype")
@Controller
@Namespace("/index")
public class SetupAction extends ExtJSActionSupport {
    @Resource(name="indexManager")
    private IndexManager indexManager;
    @Resource(name="indexSchedulerService")
    private IndexSchedulerService indexSchedulerService;
    
    private int hour;
    private int minute;
    
    public String query(){
        Map map=new HashMap();
        try{
            IndexScheduleConfig config=indexSchedulerService.getIndexScheduleConfig();

            if(config!=null && config.isEnabled()){
                map.put("state", "定时重建索引任务执行频率为每天，时间（24小时制）"+config.getScheduleHour()+":"+config.getScheduleMinute());
                map.put("hour",config.getScheduleHour());
                map.put("minute", config.getScheduleMinute());

            }else{
                map.put("state", "无定时调度任务");
            }
        }catch(Exception e){
            log.error("无定时调度任务", e);
            map.put("state", "无定时调度任务");
        }
        
        Struts2Utils.renderJson(map);
        return null;
    }
    public String rebuidAll() {        
        indexManager.rebuidAll();
        Struts2Utils.renderText("已将重建索引任务提交给后台");
        return null;
    }
    public String clearTask(){
        String result=indexSchedulerService.unSchedule();
        Struts2Utils.renderText(result);
        return null;
    }
    public String setTask(){     
        if(-1<hour && hour<24 && -1<minute && minute<60){
           String result=indexSchedulerService.schedule(hour, minute);
           Struts2Utils.renderText(result);
        } else{
            Struts2Utils.renderText("调度时间不正确");
        } 
        return null;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
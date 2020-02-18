package com.royal.recreation.controller;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.controller.base.BaseController;
import com.royal.recreation.controller.base.BaseQuery;
import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import org.bson.types.ObjectId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/record")
public class RecordController extends BaseController {

    @RequestMapping("/betInfos/{ids}")
    public String betInfos(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable String ids, Model model) {
        Object[] idArray = Arrays.stream(ids.split(",")).map(ObjectId::new).toArray();
        List<OrderInfo> orderInfoList = Mongo.buildMongo().or("_id", idArray).eq("userId", userDetails.getId()).eq("status", Status.ACTIVE).find(OrderInfo.class);
        int countUsePoint = orderInfoList.stream().mapToInt(OrderInfo::getUsePoint).sum();
        model.addAttribute("list", orderInfoList);
        model.addAttribute("countUsePoint", countUsePoint);
        return "record/print";
    }

    @RequestMapping("/search")
    public String search(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        searchGameRecord(userDetails, model, null, null, null, baseQuery);
        model.addAttribute("user", user);
        return "record/search";
    }

    @RequestMapping("/searchGameRecord")
    public String searchGameRecord(@AuthenticationPrincipal MyUserDetails userDetails, Model model, String orderNo, Integer type, Integer state, BaseQuery baseQuery) {

        Mongo query = Mongo.buildMongo();
        queryOfUser(query, userDetails, baseQuery, model);
        queryOfTime(query, baseQuery);

        if (!StringUtils.isEmpty(orderNo)) {
            query.eq("orderNo", orderNo);
        }
        if (type != null && type != 0) {
            query.eq("typeId", type);
        }
        if (state != null && state != 0) {
            query.eq("state", state);
        }
        int count = (int) query.count(OrderInfo.class);
        List<OrderInfo> orderInfoList = query.desc("_id").limit(Constant.limit, baseQuery.getPage()).find(OrderInfo.class);
        model.addAttribute("orderInfoList", orderInfoList);
        model.addAttribute("page", baseQuery.getPage());
        model.addAttribute("pages", count % Constant.limit == 0 ? count / Constant.limit : (count / Constant.limit + 1));
        model.addAttribute("queryString", getQueryString("/record/searchGameRecord"));
        return "record/searchBiao";
    }

}

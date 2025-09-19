package com.project.sleep.domain.ui;

import com.project.sleep.domain.ui.spec.XXXApiSpec;
import com.project.sleep.global.annotation.CurrentUser;
import com.project.sleep.global.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XXXController implements XXXApiSpec {

    @GetMapping("/test")
    public BaseResponse<Long> test(@CurrentUser Long userNo) {
        return BaseResponse.onSuccess(userNo);
    }
}

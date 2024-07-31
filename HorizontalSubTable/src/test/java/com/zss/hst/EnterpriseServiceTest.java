package com.zss.hst;

import com.zss.sda.service.EnterpriseService;
import com.zss.sda.model.Enterprise;
import com.zss.sda.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/22 9:47
 * @desc EnterpriseService - Test
 */
@Slf4j
public class EnterpriseServiceTest extends BaseTest {

    @Autowired
    private EnterpriseService enterpriseService;

    @Test
    public void createEnterpriseTest() {
        Enterprise build = Enterprise.builder()
                .enterpriseCode(IdUtil.getId())
                .enterpriseName("阿里麻麻")
                .address("浙江省杭州市")
                .desc("阿里麻麻集团经营多项业务，另外也从关联公司的业务和服务中取得经营商业生态系统上的支援。业务和关联公司的业务包括：淘宝网、天猫、聚划算、全球速卖通、阿里巴巴国际交易市场、1688、阿里妈妈、阿里云、蚂蚁金服、菜鸟网络等。")
                .email("alimama@123.com")
                .build();
        /*Enterprise build = Enterprise.builder()
                .enterpriseCode(IdUtil.getId())
                .enterpriseName("企鹅")
                .address("广东省深圳市")
                .desc("企鹅于1998年11月成立，是一家以互联网为基础的平台公司，通过技术丰富互联网用户的生活，助力企业数字化升级。")
                .email("penguin@123.com")
                .build();*/
        Enterprise enterprise = enterpriseService.createEnterprise(build);
        log.info("Create Enterprise: [{}]", enterprise);
    }

    @Test
    public void createEnterpriseMultiTest() {
        List<Enterprise> enterpriseList = new ArrayList<>(3);
        for (int i = 1; i <= 3; i++){
            Enterprise build = Enterprise.builder()
                    .enterpriseCode(IdUtil.getId())
                    .enterpriseName("企业" + i)
                    .address("企业" + i + "的地址")
                    .desc("企业" + i + "的描述")
                    .email("enterprise_" + i + "@123.com")
                    .build();
            enterpriseList.add(build);
        }
        List<Enterprise> enterpriseMulti = enterpriseService.createEnterpriseMulti(enterpriseList);
        enterpriseMulti.forEach(item -> System.out.println("Enterprise: " + item));
    }

    @Test
    public void getAllEnterpriseTest() {
        List<Enterprise> allEnterprise =
                enterpriseService.getAllEnterprise();
        allEnterprise.forEach(item -> System.out.println("Enterprise: " + item));
    }

}

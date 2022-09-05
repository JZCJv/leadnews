package src/main/java/com/heima.test.service.impl;

import src/main/java/com/heima.test.entity.TimeZone;
import src/main/java/com/heima.test.mapper.TimeZoneMapper;
import src/main/java/com/heima.test.service.ITimeZoneService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Time zones 服务实现类
 * </p>
 *
 * @author cjz
 * @since 2022-09-03
 */
@Service
public class TimeZoneServiceImpl extends ServiceImpl<TimeZoneMapper, TimeZone> implements ITimeZoneService {

}

package src/main/java/com/heima.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Time zones
 * </p>
 *
 * @author cjz
 * @since 2022-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TimeZone对象", description="Time zones")
public class TimeZone implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "Time_zone_id", type = IdType.AUTO)
    private Integer timeZoneId;

    private String useLeapSeconds;


}

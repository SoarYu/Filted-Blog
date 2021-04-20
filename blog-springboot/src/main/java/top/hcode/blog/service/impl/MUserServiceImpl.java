package top.hcode.blog.service.impl;

import top.hcode.blog.entity.MUser;
import top.hcode.blog.mapper.MUserMapper;
import top.hcode.blog.service.MUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser> implements MUserService {

}

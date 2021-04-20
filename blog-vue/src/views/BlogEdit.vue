<template>
  <div>
    <Header></Header>
    <div class="edit-content">
      <el-form
        :model="ruleForm"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="ruleForm.title"></el-input>
        </el-form-item>

        <el-form-item label="简介" prop="description">
          <el-input type="textarea" v-model="ruleForm.description"></el-input>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <mavon-editor ref="md"  @imgAdd="imgAdd" @imgDel="imgDel" v-model="ruleForm.content"></mavon-editor>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from "@/components/Header";
import Footer from "@/components/Footer";
export default {
  components: { Header,Footer },
  data() {
    return {
      ruleForm: {
        title: "",
        description: "",
        content: "",
        author: this.$store.getters.getUserInfo.username
      },
      rules: {
        title: [
          { required: true, message: "请输入文章标题", trigger: "blur" },
          { min: 3, max: 25, message: "长度在 3 到 25 个字符", trigger: "blur" }
        ],
        description: [
          { required: true, message: "请输入摘要（简介）", trigger: "blur" }
        ],
        content: [{ required: true, trigger: "blur" }]
      }
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          const _this = this;
          this.$axios
            .post("/blog/edit", this.ruleForm, {
              headers: {
                Authorization: localStorage.getItem("token")
              }
            })
            .then(res => {
              this.$alert("操作成功", "消息提示", {
                confirmButtonText: "确定",
                callback: action => {
                  if(this.$route.params.blogId){
                      this.$message({
                      type: "info",
                      message: '操作成功！已返回修改页面！'
                      });
                    _this.$router.push("/blog/"+this.$route.params.blogId);
                  }else{
                      this.$message({
                      type: "info",
                      message: '操作成功！已返回首页！'
                      });
                      _this.$router.push("/blog");}
                }
              });
            });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    imgAdd(pos, $file) {
        console.log(this.$route.params.blogId);
        var _this = this;
        let formdata = new FormData();
        formdata.append('image', $file);
        formdata.append('blogId', this.$route.params.blogId);
        //访问后台服务器方法
        this.$axios
            .post("/upload", formdata, {
              headers: { 'Content-Type': 'multipart/form-data' }
            }).then((res) => {
              if(res.status === 200){
                let url = res.data.data;
                _this.$refs.md.$img2Url(pos,url)
                console.log("this is url "+url)
              }
              else{
                console.log("no url")
              }
            })
    }
  },
  created() {
    let blogId = this.$route.params.blogId
    if(blogId){
      this.$axios.get("/blog/real/"+blogId).then(res=>{
        let blog = res.data.data
        this.ruleForm.id = blog.id
        this.ruleForm.title = blog.title
        this.ruleForm.description = blog.description
        this.ruleForm.content = blog.content
      })
    }
  }
};
</script>

<style scoped>
.edit-content {
  text-align: center;
  clear: both;
}

</style>
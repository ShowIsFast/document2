const plugins = ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount']

Vue.component('tinymce', {
  template: `
            <div class="tinymce-container">
              <div :id="tinymceId"></div>
              <div class="editor-custom-btn-container">
                <el-button size="mini" type="primary" icon="el-icon-upload" @click="dialogVisible = true">图片上传</el-button>
              </div>
              <el-dialog :visible.sync="dialogVisible">
                <el-upload
                  :multiple="true"
                  :file-list="fileList"
                  :show-file-list="true"
                  :on-remove="handleRemove"
                  :on-success="handleSuccess"
                  :before-upload="beforeUpload"
                  class="editor-slide-upload"
                  action="/upload/oss?folder=sku"
                  list-type="picture-card"
                >
                  <el-button size="small" type="primary">
                    Click upload
                  </el-button>
                </el-upload>
                <el-button @click="dialogVisible = false">
                  Cancel
                </el-button>
                <el-button type="primary" @click="handleSubmit">
                  Confirm
                </el-button>
              </el-dialog>
            </div>
            `,
  props: {
    id: {
      type: String,
      default: function() {
        return 'vue-tinymce-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
      }
    },
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      dialogVisible: false,
      fileList: [],
      listObj: {},
      tinymceId: this.id
    }
  },
  methods: {
    checkAllSuccess() {
      return Object.keys(this.listObj).every(item => this.listObj[item].hasSuccess)
    },
    handleSubmit() {
      console.log('tinymce.get', tinymce.get(this.tinymceId));
      const arr = Object.keys(this.listObj).map(v => this.listObj[v])
      if (!this.checkAllSuccess()) {
        this.$message('Please wait for all images to be uploaded successfully. If there is a network problem, please refresh the page and upload again!')
        return
      }
      arr.forEach(v => tinymce.get(this.tinymceId).insertContent(`<img class="wscnph" src="${v.url}" >`))
      this.listObj = {}
      this.fileList = []
      this.dialogVisible = false
    },
    handleSuccess(response, file) {
      console.log('handle-success', response);
      const uid = file.uid
      const objKeyArr = Object.keys(this.listObj)
      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        if (this.listObj[objKeyArr[i]].uid === uid) {
          this.listObj[objKeyArr[i]].url = response
          this.listObj[objKeyArr[i]].hasSuccess = true
          return
        }
      }
    },
    handleRemove(file) {
      const uid = file.uid
      const objKeyArr = Object.keys(this.listObj)
      for (let i = 0, len = objKeyArr.length; i < len; i++) {
        if (this.listObj[objKeyArr[i]].uid === uid) {
          delete this.listObj[objKeyArr[i]]
          return
        }
      }
    },
    beforeUpload(file) {
      console.log('file', file);
      const _self = this
      const _URL = window.URL || window.webkitURL
      const fileName = file.uid
      this.listObj[fileName] = {}
      return new Promise((resolve, reject) => {
        const img = new Image()
        img.src = _URL.createObjectURL(file)
        img.onload = function () {
          _self.listObj[fileName] = { hasSuccess: false, uid: file.uid, width: this.width, height: this.height }
        }
        resolve(true)
      })
    }
  },
  created() {
    tinymce.init({
      selector: `#${this.tinymceId}`,
      language: 'zh_CN',
      plugins,
      body_class: 'panel-body',
      init_instance_callback: editor => {
        if (this.value) {
          editor.setContent(this.value)
        }

        editor.on('NodeChange Change KeyUp SetContent', () => {
          this.$emit('input', editor.getContent())
        })
      }
    });
  }
})

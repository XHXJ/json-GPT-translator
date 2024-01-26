<template>
  <div>
    <el-card>
      <el-tabs v-model="activeName">
        <el-tab-pane label="chagpt配置" name="chagpt">
          <el-form :model="chagptForm.data" label-width="200px">
            <div class="file-import-export">
              <el-button type="success" @click="onSubmit">保存</el-button>
              <el-upload class="item" action="/api/openai-properties/chat-gpt-import" :on-success="getConfigData"
                         :show-file-list="false" :limit="1">
                <template #trigger>
                  <el-button type="danger">导入</el-button>
                </template>
              </el-upload>
              <el-button class="item" type="primary" @click="onImport">导出</el-button>
            </div>
            <el-form-item label="多行翻译prompt:">
              <el-input v-model="chagptForm.data.promptMultipleTranslations" autosize type="textarea"/>
            </el-form-item>
            <el-form-item label="单条翻译prompt:">
              <el-input v-model="chagptForm.data.promptSingleTranslations" autosize type="textarea"/>
            </el-form-item>
            <el-form-item label="单条翻译JSON Key定义:">
              <el-input v-model="chagptForm.data.promptSingleJsonKey" style="width: 240px"></el-input>
              <div style="padding-left: 20px">
                <el-tooltip placement="top">
                  <template #content>
                    如果response_format配置为json_object，
                    <br/>则需要配置为你单条翻译测试返回的JSON
                    <br/>中能获取到译文的key值，配置为空则不启用。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-divider/>
            <h4>模型配置:</h4>
            <el-form-item label="openai模型配置:">
              <el-select v-model="chagptForm.data.model"
                         filterable
                         allow-create
                         default-first-option
                         style="width: 240px"
                         placeholder="请选择您的gpt模型(可自定义输入)">
                <el-option label="gpt-3.5-turbo-1106" value="gpt-3.5-turbo-1106"/>
                <el-option label="gpt-3.5-turbo" value="gpt-3.5-turbo"/>
                <el-option label="gpt-3.5-turbo-16k" value="gpt-3.5-turbo-16k"/>
                <el-option label="gpt-3.5-turbo-instruct" value="gpt-3.5-turbo-instruct"/>
                <el-option label="gpt-3.5-turbo-0613" value="gpt-3.5-turbo-0613"/>
                <el-option label="gpt-3.5-turbo-16k-0613" value="gpt-3.5-turbo-16k-0613"/>
                <el-option label="gpt-3.5-turbo-0301" value="gpt-3.5-turbo-0301"/>
                <el-option label="gpt-4-0125-preview" value="gpt-4-0125-preview"/>
                <el-option label="gpt-4-turbo-preview" value="gpt-4-turbo-preview"/>
                <el-option label="gpt-4-1106-preview" value="gpt-4-1106-preview"/>
                <el-option label="gpt-4-vision-preview" value="gpt-4-vision-preview"/>
                <el-option label="gpt-4" value="gpt-4"/>
                <el-option label="gpt-4-0613" value="gpt-4-0613"/>
                <el-option label="gpt-4-32k" value="gpt-4-32k"/>
                <el-option label="gpt-4-32k-0613" value="gpt-4-32k-0613"/>
              </el-select>
              <div style="padding-left: 20px">
                <el-tooltip placement="top">
                  <template #content>
                    如果官网的模型更新，可以手动输入想要使用的模型。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-form-item label="response_format 配置:">
              <!--              <el-input v-model="chagptForm.data.responseFormat" style="width: 240px"></el-input>-->
              <el-radio-group v-model="chagptForm.data.responseFormat">
                <el-radio label="" size="large">无</el-radio>
                <el-radio label="json_object" size="large">json_object</el-radio>
              </el-radio-group>
              <div style="padding-left: 20px">
                <el-tooltip placement="top">
                  <template #content>
                    如果选用的openai模型支持 <a style="color: red" href="https://platform.openai.com/docs/guides/text-generation/json-mode">JSON mode</a>
                    <br/> 则选用 json_object 选项，提升翻译成功率。
                    <br/>
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-form-item label="API代理地址:">
              <el-input v-model="chagptForm.data.apiHost" style="width: 240px"/>
            </el-form-item>
            <el-divider/>
            <h4>核心配置:</h4>
            <el-form-item label="topP">

              <div class="core-configuration-form">
                <el-slider v-model="chagptForm.data.topP" show-input :min="0" :max="1" :step="0.01"
                           class="item"/>
              </div>
              <div style="padding-right: 20px">
                <el-tooltip placement="top">
                  <template #content> 使用温度采样的替代方法称为核心采样，
                    <br/>其中模型考虑具有top_p概率质量的令牌的结果。
                    <br/>因此，0.1 意味着只考虑包含前 10% 概率质量的代币。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-form-item label="temperature">
              <div class="core-configuration-form">
                <el-slider v-model="chagptForm.data.temperature" show-input :min="0" :max="2" :step="0.01"
                           class="item"/>
              </div>
              <div style="padding-right: 20px">
                <el-tooltip placement="top">
                  <template #content> 使用什么取样温度，0到2之间。
                    <br/>较高的值(如0.8)将使输出更加随机，
                    <br/>而较低的值(如0.2)将使输出更加集中和确定。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-form-item label="presencePenalty">
              <div class="core-configuration-form">
                <el-slider v-model="chagptForm.data.presencePenalty" show-input :min="0" :max="2" :step="0.01"
                           class="item"/>
              </div>
              <div style="padding-right: 20px">
                <el-tooltip placement="top">
                  <template #content>
                    根据新词是否出现在文本中，
                    <br/>惩罚多少。增加模型讨论新主题的可能性。
                    <br/>0到2之间的值。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-form-item label="frequencyPenalty">

              <div class="core-configuration-form">
                <el-slider v-model="chagptForm.data.frequencyPenalty" show-input :min="0" :max="2" :step="0.01"
                           class="item"/>
              </div>
              <div>
                <el-tooltip placement="top">
                  <template #content>
                    到目前为止，根据新标记在文本中的现有频率，
                    <br/>应该惩罚多少个新标记。
                    <br/>降低模型逐字重复同一行的可能性。
                    <br/>0到2之间的值。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
            <el-divider/>
            <h4>代理配置:</h4>
            <el-form-item label="代理类型: ">
              <el-radio-group v-model="chagptForm.data.proxyType" class="ml-4">
                <el-radio label="SOCKS" size="large">SOCKS</el-radio>
                <el-radio label="HTTP" size="large">HTTP</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="地址:" style="width: 40%">
              <el-input v-model="chagptForm.data.proxyUlr"/>
            </el-form-item>
            <el-form-item label="端口:" style="width: 30%">
              <el-input v-model="chagptForm.data.proxyPort" type="number"/>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="测试" name="test">
          <el-form :model="chagptForm.data" label-width="200px">
            <el-form-item label="多行翻译prompt:">
              <el-input v-model="chagptForm.data.promptMultipleTranslations" autosize type="textarea"/>
            </el-form-item>
            <el-form-item label="单条翻译prompt:">
              <el-input v-model="chagptForm.data.promptSingleTranslations" autosize type="textarea"/>
            </el-form-item>
            <el-form-item label="单条翻译JSON Key定义:">
              <el-input v-model="chagptForm.data.promptSingleJsonKey" style="width: 240px"></el-input>
              <div style="padding-left: 20px">
                <el-tooltip placement="top">
                  <template #content>
                    如果response_format配置为json_object，
                    <br/>则需要配置为你单条翻译测试返回的JSON
                    <br/>中能获取到译文的key值，配置为空则不启用。
                  </template>
                  <IconifyIconOffline :icon="InfoFilled"/>
                </el-tooltip>
              </div>
            </el-form-item>
          </el-form>
          <el-divider/>
          <h5>多行翻译测试数据: </h5>
          <div class="file-import-export">
            <el-button @click="onAddTestDataList" type="primary" link>新增翻译数据</el-button>
          </div>
          <el-table :data="chatGptTest.data.translationDataList" style="width: 100%">
            <el-table-column prop="id" label="id" width="100"/>
            <el-table-column prop="originalText" label="原文">
              <template #default="scope">
                <el-input
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  v-model="scope.row.originalText"
                  type="textarea"
                  placeholder="指定翻译原文"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="danger" size="small" @click="onTestDataListDelete(scope.row.id) "
                >删除
                </el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-divider/>
          <div style="margin-top: 20px">
            <h5>单行测试数据: </h5>
            <br>
            <el-input
              :autosize="{ minRows: 2, maxRows: 4 }"
              v-model="chatGptTest.data.translationData.originalText"
              type="textarea"
              placeholder="指定翻译原文"
            />
          </div>
          <el-divider/>
          <div style="margin-top: 40px" v-if="displayResults">
            <el-card>
              <template #header>
                <span>测试结果:</span>
              </template>
              <div>
                <div style="font-size: 15px;margin-bottom: 20px">多行翻译结果:</div>
                <vue-json-pretty :data="chatGptConfigTestRespVo.data.promptMultipleTranslationsResult"/>
                <el-tag v-if="!chatGptConfigTestRespVo.data.promptMultipleTranslationsSuccess" type="danger">
                  多行翻译结果不通过,结果不符合Json格式
                </el-tag>
                <el-divider/>
                <div style="font-size: 15px;margin-bottom: 20px">单行翻译结果:</div>
                <div>{{ chatGptConfigTestRespVo.data.promptSingleTranslationsResult }}</div>
              </div>

            </el-card>
          </div>

          <div class="file-import-export" style="margin-top: 40px">
            <el-button type="success" @click="onSubmit">保存配置文件</el-button>
            <el-button @click="onTestTranslation" type="primary">翻译测试</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import InfoFilled from "@iconify-icons/ep/info-filled";
import {getChatGptConfig, testChatGptConfig, updateChatGptConfig} from "@/api/configure";
import {message} from "@/utils/message";
import {downloadByData} from "@pureadmin/utils";
import axios from "axios";
import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';
import {ElLoading} from "element-plus";

//测试结果
interface ChatGptConfigTestRespInterface {
  promptSingleTranslationsResult: string;
  promptMultipleTranslationsResult: object;
  promptMultipleTranslationsSuccess: boolean;
}

//控制返回结果显示
const displayResults = ref(false);

//测试返回结果
const chatGptConfigTestRespVo = reactive<{ data: ChatGptConfigTestRespInterface }>({});

// 模型测试相关
interface TranslationData {
  id: number | null;
  originalText: string;
}

const chatGptTest = reactive<{
  data: {
    translationDataList: TranslationData[],
    translationData: TranslationData
  }
}>({
  data: {
    translationDataList: [
      {
        id: 0,
        originalText: '武器戦技を継承する\n'
      },
      {
        id: 1,
        originalText: 'アステルが目を開けて、光になって天に昇って行く二人を見送る\n'
      },
      {
        id: 2,
        originalText: 'performMiss'
      }
    ],
    translationData: {
      id: 0,
      originalText: 'もし、今の状況が自分らしくないことの連続で、好きになれないなら、どうすれば、変えられるかを真剣に考えてみよう。そしないと問題はちっとも解決しない。'
    }
  }
});

const chagptForm = reactive({
  data: {
    promptSingleTranslations: '',
    promptMultipleTranslations: '',
    topP: 0,
    temperature: 0,
    presencePenalty: 0,
    frequencyPenalty: 0,
    proxyUlr: '',
    proxyPort: 0,
    proxyType: '',
    model: '',
    responseFormat: '',
    promptSingleJsonKey: '',
    apiHost: ''
  }
})
const onTestTranslation = () => {
  const loading = ElLoading.service({
    lock: true,
    text: '请耐心等待测试完成',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  // //更新配置文件
  // onSubmit()
  //开始测试
  testChatGptConfig({...chatGptTest.data, ...chagptForm.data}).then(res => {
    if (res.code === 0) {
      message('测试成功', {type: 'success'})
      //更测试结果
      displayResults.value = true
      chatGptConfigTestRespVo.data = res.data
    } else {
      message(res.msg, {type: 'error'})
    }

    loading.close()
  })
}
//删除测试数据
const onTestDataListDelete = (id: number) => {
  chatGptTest.data.translationDataList = chatGptTest.data.translationDataList.filter(item => item.id !== id)
}

//多行测试新增测试数据
const onAddTestDataList = () => {
  chatGptTest.data.translationDataList.push({
    id: chatGptTest.data.translationDataList.length,
    originalText: ''
  })
}

const onSubmit = () => {
  updateChatGptConfig(chagptForm.data).then(res => {
    if (res.code === 0) {
      message('修改配置成功', {type: 'success'})
    } else {
      message(res.msg, {type: 'error'})
    }
    getConfigData()
  })
}
// 导出配置文件
const onImport = () => {
  axios
    .get("/api/openai-properties/chat-gpt-export", {
      responseType: "blob"
    })
    .then((response) => {
      let disposition = response.headers['content-disposition'];
      let filename = 'config.json'; // 默认文件名

      if (disposition) {
        let dispositionFilename = disposition.split('filename=')[1]?.trim();
        if (dispositionFilename) {
          filename = dispositionFilename;
        }
      }
      downloadByData(response.data, filename);
    });
}
const getConfigData = () => {
  getChatGptConfig().then(res => {
    chagptForm.data = res.data
  })
}
onMounted(() => {
  //获取配置文件
  getConfigData()
})

const activeName = ref('chagpt')
</script>


<style scoped lang="scss">
.file-import-export {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  margin-bottom: 20px;

  .item {
    margin-left: 20px
  }
}

.core-configuration-form {
  width: 50%;
  margin-left: 20px;

  .item {
    width: 95%
  }
}
</style>

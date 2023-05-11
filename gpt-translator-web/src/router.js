import {createRouter, createWebHashHistory} from "vue-router"
import Home from '@/components/home/Home.vue';
import UploadFileStart from '@/components/uploadfile/UploadFileStart.vue';
import ConfigureKey from '@/components/configure/key/Configurekey.vue';
import ConfigurePrompt from '@/components/configure/Prompt/ConfigurePrompt.vue';
import Query from '@/components/query/Query.vue';
import Export from '@/components/export/Export.vue';

const routes = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            component: Home
        },
        {
            path: '/upload-file-start',
            component: UploadFileStart
        },
        {
            path: '/configure',
            children: [
                {
                    path: 'key',
                    component: ConfigureKey
                },
                {
                    path: 'prompt',
                    component: ConfigurePrompt
                }
            ]
        },
        {
            path: '/query',
            component: Query
        },
        {
            path: '/export',
            component: Export
        }
    ]
});

export default routes;

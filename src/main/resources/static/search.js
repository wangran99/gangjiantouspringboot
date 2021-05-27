let api = [];
api.push({
    alias: 'ApplyController',
    order: '1',
    link: '&lt;p&gt;申请表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;申请表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'ApprovalFlowController',
    order: '2',
    link: '&lt;p&gt;审批流程定义表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;审批流程定义表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'ApproverController',
    order: '3',
    link: '&lt;p&gt;审批人表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;审批人表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'AuthorizationController',
    order: '4',
    link: '&lt;p&gt;_认证鉴权请求控制器&lt;/p&gt;',
    desc: '&lt;p&gt; 认证鉴权请求控制器&lt;/p&gt;',
    list: []
})
api[3].list.push({
    order: '1',
    desc: 'PC/手机 We码认证',
});
api[3].list.push({
    order: '2',
    desc: 'H5轻应用鉴权登录 开放平台文档：https://open.welink.huaweicloud.com/docs/#/qdmtm8/tj778t/wk8q1m?type=third',
});
api[3].list.push({
    order: '3',
    desc: '后台web管理页面免登',
});
api[3].list.push({
    order: '4',
    desc: '用户退出(删除token)',
});
api.push({
    alias: 'CarbonCopyController',
    order: '5',
    link: '&lt;p&gt;审批抄送表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;审批抄送表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'DepartmentController',
    order: '6',
    link: '&lt;p&gt;部门表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;部门表 前端控制器&lt;/p&gt;',
    list: []
})
api[5].list.push({
    order: '1',
    desc: '获取所有部门列表',
});
api[5].list.push({
    order: '2',
    desc: '按结构获取所有部门列表',
});
api.push({
    alias: 'FileController',
    order: '7',
    link: '&lt;p&gt;上传文件_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;上传文件 前端控制器&lt;/p&gt;',
    list: []
})
api[6].list.push({
    order: '1',
    desc: '',
});
api[6].list.push({
    order: '2',
    desc: '多个文件上传',
});
api.push({
    alias: 'MenuController',
    order: '8',
    link: '&lt;p&gt;菜单表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;菜单表 前端控制器&lt;/p&gt;',
    list: []
})
api[7].list.push({
    order: '1',
    desc: '获取所有的菜单',
});
api[7].list.push({
    order: '2',
    desc: '获取我的菜单',
});
api.push({
    alias: 'PositionController',
    order: '9',
    link: '&lt;p&gt;岗位定义表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;岗位定义表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'RoleController',
    order: '10',
    link: '&lt;p&gt;角色定义表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;角色定义表 前端控制器&lt;/p&gt;',
    list: []
})
api[9].list.push({
    order: '1',
    desc: '查询定义的角色信息',
});
api.push({
    alias: 'RoleMenuController',
    order: '11',
    link: '&lt;p&gt;角色菜单对应表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;角色菜单对应表 前端控制器&lt;/p&gt;',
    list: []
})
api[10].list.push({
    order: '1',
    desc: '增加新角色和对应的菜单',
});
api[10].list.push({
    order: '2',
    desc: '修改绑定角色和菜单',
});
api[10].list.push({
    order: '3',
    desc: '删除角色以及绑定的菜单',
});
api.push({
    alias: 'TestController',
    order: '12',
    link: '测试服务是否正常启动',
    desc: '测试服务是否正常启动',
    list: []
})
api[11].list.push({
    order: '1',
    desc: '测试返回字符串',
});
api[11].list.push({
    order: '2',
    desc: '测试后台welink调用是否成功',
});
api[11].list.push({
    order: '3',
    desc: '测试数据库读取是否正常',
});
api[11].list.push({
    order: '4',
    desc: '',
});
api[11].list.push({
    order: '5',
    desc: '',
});
api[11].list.push({
    order: '6',
    desc: '',
});
api[11].list.push({
    order: '7',
    desc: '',
});
api.push({
    alias: 'TodoTaskController',
    order: '13',
    link: '&lt;p&gt;待办消息表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;待办消息表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'UserController',
    order: '14',
    link: '&lt;p&gt;用户表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;用户表 前端控制器&lt;/p&gt;',
    list: []
})
api[13].list.push({
    order: '1',
    desc: '根据条件查询用户',
});
api.push({
    alias: 'UserPositionController',
    order: '15',
    link: '&lt;p&gt;用户岗位表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;用户岗位表 前端控制器&lt;/p&gt;',
    list: []
})
api.push({
    alias: 'UserRoleController',
    order: '16',
    link: '&lt;p&gt;用户角色表_前端控制器&lt;/p&gt;',
    desc: '&lt;p&gt;用户角色表 前端控制器&lt;/p&gt;',
    list: []
})
api[15].list.push({
    order: '1',
    desc: '获取当前用户的角色',
});
api[15].list.push({
    order: '2',
    desc: '获取用户角色列表',
});
api[15].list.push({
    order: '3',
    desc: '修改用户角色',
});
api[15].list.push({
    order: '4',
    desc: '删除用户某个角色',
});
api.push({
    alias: 'dict',
    order: '17',
    link: 'dict_list',
    desc: '数据字典',
    list: []
})
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code == 13) {
        const search = document.getElementById('search');
        const searchValue = search.value;
        let searchArr = [];
        for (let i = 0; i < api.length; i++) {
            let apiData = api[i];
            const desc = apiData.desc;
            if (desc.indexOf(searchValue) > -1) {
                searchArr.push({
                    order: apiData.order,
                    desc: apiData.desc,
                    link: apiData.link,
                    list: apiData.list
                });
            } else {
                let methodList = apiData.list || [];
                let methodListTemp = [];
                for (let j = 0; j < methodList.length; j++) {
                    const methodData = methodList[j];
                    const methodDesc = methodData.desc;
                    if (methodDesc.indexOf(searchValue) > -1) {
                        methodListTemp.push(methodData);
                        break;
                    }
                }
                if (methodListTemp.length > 0) {
                    const data = {
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: methodListTemp
                    };
                    searchArr.push(data);
                }
            }
        }
        let html;
        if (searchValue == '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchArr,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiData, liClass, display) {
    let html = "";
    let doc;
    if (apiData.length > 0) {
        for (let j = 0; j < apiData.length; j++) {
            html += '<li class="'+liClass+'">';
            html += '<a class="dd" href="#_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
            html += '<ul class="sectlevel2" style="'+display+'">';
            doc = apiData[j].list;
            for (let m = 0; m < doc.length; m++) {
                html += '<li><a href="#_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + doc[m].desc + '</a> </li>';
            }
            html += '</ul>';
            html += '</li>';
        }
    }
    return html;
}
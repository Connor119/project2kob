const ModuleRecord = {
    state: {
        // 判断这是录像还是对战
        is_record: false,
        a_steps: "",
        b_steps: "",
        record_loser: "",
    },
    getters: {},
    mutations: {
        updateIsRecord(state, is_record) {
            state.is_record = is_record;
        },
        updateSteps(state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
        updateRecordLoser(state, record_loser) {
            state.record_loser = record_loser;
        }
    },
    actions: {},
    modules: {}
}

export default ModuleRecord;
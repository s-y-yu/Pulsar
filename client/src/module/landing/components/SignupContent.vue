<template>
  <div id="signup_content">
    <!-- 상위 컴포넌트에서 v-model은 하위 컴포넌트에서 emit해 바인딩할 수 있습니다.
         v-model이 하위 컴포넌트에서의 emit + 그 하위 컴포넌트로 pass prop인 것을 이용한 것입니다. -->
    <h1>PULSAR의 멤버가 되세요!</h1>
    <p id="caption">
      멤버로 가입하시면 모든 서비스를 이용하실 수 있습니다.
    </p>
    <form @keyup.enter="handleBtn">
      <text-input
        :input-name="'Email'"
        :placeholder="'Email'"
        :inputType="'email'"
        :margin="'1rem'"
        v-model="email"
      />
      <text-input
        :input-name="'Password'"
        :placeholder="'Password'"
        :inputType="'password'"
        :caption="'영문 소문자, 숫자, 특수문자 포함 8자리 이상'"
        v-model="password"
      />
      <text-input
        :input-name="'Nickname'"
        :placeholder="'Nickname'"
        :inputType="'text'"
        :margin="'1rem'"
        v-model="nickname"
      />
      <!--tag components-->
      <tag-form
        v-model="selectedTag"
        :selectable="true"
        :tags="concerns"
        :caption="'운동에 대한 가장 큰 고민이 무엇인가요?'"
        @tagform="setConcerns"
      />
      <!-- <input type="file" @change="setFile" /> -->
      <square-button
        :theme="'point'"
        :value="'SIGN UP'"
        @handle-click="handleBtn"
        id="signup_content_signupBtn"
      />
    </form>
    <router-link to="/">
      <square-button
        :theme="'white'"
        :value="'이미 멤버로 가입되어 있어요'"
      />
    </router-link>
  </div>
</template>

<style scoped>
@import url('../../../assets/css/init.css');
@import url('../../../assets/css/root.css');
@import url('../../../assets/css/typography.css');

#signup_content {
  padding: 1.5rem;
}
#caption {
  margin: 0 0 2rem 0;
}
#signup_content_signupBtn {
  margin-bottom: 1rem;
}
</style>

<script>
import TagForm from '../../common/TagForm.vue';
import SquareButton from '../../common/SquareButton.vue';
import TextInput from '../../common/TextInput.vue';

export default {
  name: 'SignupContent',
  components: { TextInput, SquareButton, TagForm },
  props: { concerns: Array },
  data() {
    return {
      //각 커스텀 인풋 컴포넌트에서 받은 입력값을 v-model로 양방향 바인딩합니다.
      email: '',
      password: '',
      nickname: '',
      selectedTag: [],
      profileImg: null,
    };
  },
  methods: {
    handleBtn() {
      //로그인을 시도하기 위해 위쪽으로 로그인 이벤트와 값을 보내고,
      //양방향 바인딩되어 있는 데이터 쪽의 값을 초기화해 인풋을 빈 값으로 만듭니다.
      const email = this.email;
      const password = this.password;
      const nickname = this.nickname;
      const selectedTag = this.selectedTag;
      const profileImg = this.profileImg;
      const form = {
        email,
        password,
        nickname,
        selectedTag,
      };
      this.$emit('signup', form, profileImg);
      this.email = '';
      this.password = '';
      this.nickname = '';
      this.selectedTag = [];
      this.profileImg = null;
    },
    setConcerns(concerns) {
      this.selectedTag = concerns;
    },
    // setFile(e) {
    //   const file = e.target.files[0];
    //   this.profileImg = file;
    // },
  },
};
</script>

import { useRef, useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import * as utils from 'utils';
import {
  TopNavigation,
  RegistInfoInput,
  RegistInfoTitle,
  RegistInfoFrame,
  RegistInfoCheckBox,
} from 'components';
import { RegistInfo } from 'types/ProfileInfo';
import { MemberApi } from 'api';
import {
  setAccessToken,
  setRefreshToken,
  useAppDispatch,
  useAppSelector,
} from 'hooks';

const RegistInfo = () => {
  const { state } = useLocation();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const inputRefForBirthYear = useRef<HTMLInputElement>(null);
  const [gender, setGender] = useState<string>(utils.GENDER.MALE);

  const memberId = useAppSelector(state => state.auth.memberId);
  useEffect(() => {
    console.log(`init, ${memberId}`);
  }, []);

  const [isNextButtonDisabled, setNextButtonDisabled] = useState<boolean>(true);

  const handleSelectChange = (value: string) => {
    setGender(value);
  };

  const inputCheck = (birthYearInput: string | undefined) => {
    if (
      state.height &&
      state.weight &&
      birthYearInput &&
      Number(birthYearInput) > 1900 &&
      Number(birthYearInput) < 2024
    ) {
      return true;
    }
    return false;
  };

  const handleInputChange = () => {
    const birthYearInput = inputRefForBirthYear.current?.value;
    setNextButtonDisabled(!inputCheck(birthYearInput));
  };

  const handleNextPage = async () => {
    if (
      inputRefForBirthYear.current &&
      inputCheck(inputRefForBirthYear.current?.value)
    ) {
      try {
        const postData: RegistInfo = {
          height: Number(state.height),
          weight: Number(state.weight),
          gender: gender === utils.GENDER.MALE ? 'M' : 'F',
          birthYear: Number(inputRefForBirthYear.current.value),
          memberId: Number(memberId),
        };
        console.log(postData);
        const res = await MemberApi.registInfo(postData);
        console.log(res);
        const data = res.data.results;
        console.log('postData ', postData);
        console.log(data);

        dispatch(setAccessToken(data.accessToken));
        dispatch(setRefreshToken(data.refreshToken));
        navigate(utils.URL.RESULT.REGIST);
      } catch (error) {
        console.error('가입정보 전송 에러:', error);
      }
      // navigate(utils.URL.RESULT.REGIST);
    }
  };

  return (
    <RegistInfoFrame.Wrap>
      <TopNavigation navigationTo={utils.URL.LOGIN.REGIST_INFO.PHYSICAL} />
      <RegistInfoTitle
        mainTitle={'나이와 성별을 입력해주세요'}
        subTitle={'칼로리 계산에 사용됩니다'}
      />
      <RegistInfoFrame.InputSection>
        <RegistInfoInput
          holderText="예) 1990"
          inputRef={inputRefForBirthYear}
          onChange={handleInputChange}
          title="출생 연도"
        />
        <RegistInfoCheckBox
          title="성별"
          value={gender}
          onChange={handleSelectChange}
        />
      </RegistInfoFrame.InputSection>
      <RegistInfoFrame.BottomSection>
        <RegistInfoFrame.NextButton
          disabled={isNextButtonDisabled}
          onClick={handleNextPage}
        >
          가입 완료하기
        </RegistInfoFrame.NextButton>
      </RegistInfoFrame.BottomSection>
    </RegistInfoFrame.Wrap>
  );
};

export default RegistInfo;

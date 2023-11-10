import styled from 'styled-components';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import * as utils from 'utils';
import { ConfirmButton, RecordReport } from 'components';
import SaveSvg from 'assets/icons/save.svg?react';
import { store, useAppDispatch, useCapture } from 'hooks';
import PloggingDone from 'components/plogging/PloggingDone';
import { deleteAllPlogging } from 'hooks/store/usePlogging';

const PloggingReport = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { handleCaptureClick, captureRef } = useCapture();
  const [showLoading, setLoading] = useState(true);

  const handleDonePlogging = () => {
    dispatch(deleteAllPlogging());
    navigate(utils.URL.MYPAGE.HOME);
  };

  useEffect(() => {
    const loadingTimer = setTimeout(() => {
      setLoading(false);
    }, 3000);

    return () => {
      clearTimeout(loadingTimer);
    };
  }, []);

  if (showLoading) {
    return <PloggingDone />;
  }

  return (
    <S.Wrap>
      <S.Content ref={captureRef}>
        <S.TitleFrame>
          <S.MainTitle>플로깅 완료</S.MainTitle>
          <S.CloseButton onClick={() => navigate(utils.URL.PLOGGING.LOBBY)}>
            닫기
          </S.CloseButton>
        </S.TitleFrame>
        <S.SubText>플로깅 기록을 확인해주세요</S.SubText>

        <S.SubTitle>나의 이동 경로</S.SubTitle>
        <S.Image src={store.getState().plogging.routeImageUrl!} />

        <S.SubTitle>기록</S.SubTitle>
        <RecordReport />

        <S.SaveImage onClick={handleCaptureClick}>
          <SaveSvg />
          이미지로 저장하기
        </S.SaveImage>
      </S.Content>
      <S.BottomFrame>
        <ConfirmButton text="마이페이지로 이동" onClick={handleDonePlogging} />
      </S.BottomFrame>
    </S.Wrap>
  );
};

const S = {
  Wrap: styled.div`
    display: flex;
    flex-direction: column;
    overflow: hidden;
    width: 100%;
    height: 100vh;
    padding-top: 10px;
  `,

  Content: styled.div`
    padding: 0 24px;
    overflow-y: scroll;
    &::-webkit-scrollbar {
      display: none;
    }
  `,

  Image: styled.img`
    width: 100%;
    margin-top: 22px;
  `,

  TitleFrame: styled.div`
    margin-top: 25px;
    display: flex;
    justify-content: space-between;
  `,

  CloseButton: styled.div`
    cursor: pointer;
    color: ${({ theme }) => theme.color.main};
    font-size: ${({ theme }) => theme.font.size.focus2};
    font-family: ${({ theme }) => theme.font.family.focus2};
    font-weight: ${({ theme }) => theme.font.weight.body2};
  `,

  MainTitle: styled.div`
    font-size: ${({ theme }) => theme.font.size.display1};
    font-family: ${({ theme }) => theme.font.family.display1};
    font-weight: ${({ theme }) => theme.font.weight.body2};
    line-height: ${({ theme }) => theme.font.lineheight.display1};
    color: ${({ theme }) => theme.color.dark};
  `,

  SubText: styled.div`
    margin-top: 10px;
    color: ${({ theme }) => theme.color.gray2};
    font-size: ${({ theme }) => theme.font.size.body2};
    font-family: ${({ theme }) => theme.font.family.body2};
    font-weight: ${({ theme }) => theme.font.weight.body2};
    line-height: ${({ theme }) => theme.font.lineheight.body2};
  `,

  SubTitle: styled.div`
    margin-top: 40px;
    color: ${({ theme }) => theme.color.dark};
    font-size: ${({ theme }) => theme.font.size.body1};
    font-family: ${({ theme }) => theme.font.family.focus2};
    font-weight: ${({ theme }) => theme.font.weight.body2};
    line-height: ${({ theme }) => theme.font.lineheight.body2};
  `,

  BottomFrame: styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    bottom: 0;
    width: 100%;
    margin: auto 0 50px 0;
  `,

  SaveImage: styled.div`
    cursor: pointer;
    margin: 16px 0;
    display: flex;
    justify-content: flex-end;
    gap: 6px;
    align-items: center;
    font-size: ${({ theme }) => theme.font.size.body3};
    font-family: ${({ theme }) => theme.font.family.focus3};
    font-weight: ${({ theme }) => theme.font.weight.body2};
    color: ${({ theme }) => theme.color.gray2};
  `,
};
export default PloggingReport;

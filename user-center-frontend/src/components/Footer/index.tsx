import {GithubOutlined} from '@ant-design/icons';
import {DefaultFooter} from '@ant-design/pro-layout';
//import {PLANET_LINK} from "@/constants";

const Footer: React.FC = () => {
  const defaultMessage = 'lnc';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        // {
        //   key: 'planet',
        //   title: '知识星球',
        //   href: PLANET_LINK,
        //   blankTarget: true,
        // },
        // {
        //   key: 'codeNav',
        //   title: '编程导航',
        //   href: 'https://www.code-nav.cn',
        //   blankTarget: true,
        // },
        {
          key: 'github',
          title: <><GithubOutlined/> GitHub</>,
          href: 'https://github.com/JOHNCHEN27',
          blankTarget: true,
        },

      ]}
    />
  );
};

export default Footer;

import React, { useContext } from 'react';
import { Link, useHistory } from 'react-router-dom';
import { TrustyContext } from '../../Templates/TrustyApp/TrustyApp';

interface TrustyLinkProps
  extends React.AnchorHTMLAttributes<HTMLAnchorElement> {
  url: string;
  children: React.ReactNode;
}

const TrustyLink = (props: TrustyLinkProps) => {
  const { url, children, ...rest } = props;
  const history = useHistory();
  const { config } = useContext(TrustyContext);
  const pushUrlToHistory = () => {
    history.push(url);
  };

  return (
    <>
      {config.useHrefLinks ? (
        <Link {...rest} to={url}>
          {children}
        </Link>
      ) : (
        <a {...rest} onClick={pushUrlToHistory}>
          {children}
        </a>
      )}
    </>
  );
};

export default TrustyLink;

import { useEffect, useState } from 'react';
import { debounce } from 'lodash';

const useCFSizes = (containerRef?: HTMLDivElement) => {
  const [containerWidth, setContainerWidth] = useState(0);
  const [containerHeight, setContainerHeight] = useState(0);
  const [windowSize, setWindowSize] = useState(0);

  useEffect(() => {
    const getContainerWidth = () => {
      const size = containerRef ? containerRef.clientWidth - 60 : 0;
      return size < 768 ? 768 : size;
    };

    const getContainerHeight = () => {
      return containerRef ? containerRef.clientHeight : 0;
    };

    setContainerWidth(getContainerWidth());
    setContainerHeight(getContainerHeight());
    setWindowSize(window.innerWidth);

    const handleWindowResize = debounce(() => {
      setContainerWidth(getContainerWidth());
      setContainerHeight(getContainerHeight());
      setWindowSize(window.innerWidth);
    }, 150);

    window.addEventListener('resize', handleWindowResize);
    return () => window.removeEventListener('resize', handleWindowResize);
  }, [containerRef]);

  return { containerWidth, containerHeight, windowSize };
};

export default useCFSizes;

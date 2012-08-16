module XMLTools (
	makeXMLString,
	showContent,
	showElement,
	childrenE,
	createDirectoryIfNotExist,
	getAttr,
	isElemName,
	getElemText
) where

import Text.XML.HaXml hiding (when)
import Data.List
import System.Directory
import Control.Monad

createDirectoryIfNotExist :: FilePath -> IO ()
createDirectoryIfNotExist dir = do
	b <- doesDirectoryExist dir
	when (not b) $ createDirectory dir

makeXMLString :: [Element i] -> String
makeXMLString elem =
	header ++ "<direction from=\"lojban\" to=\"English\">" ++ body ++
		"</direction>\n"
	where
	body = intercalate "\n" $ map showElement elem

header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"

showContent :: Content i -> String
showContent (CElem elem info) = showElement elem
showContent (CString ws cd info) = cd
showContent (CRef ref info) = show ref
showContent (CMisc misc info) = "misc"

showElement :: Element i -> String
showElement (Elem name attrs conts) =
	"<" ++ showQName name ++ " " ++
	intercalate " " (map showAttr attrs) ++ ">" ++
	concatMap showContent conts ++
	"</" ++ showQName name ++ ">"

showQName :: QName -> String
showQName (N n) = n

showAttr :: Attribute -> String
showAttr (N name, attv) = name ++ "=\"" ++ show attv ++ "\""

childrenE :: Element i -> [Element i]
childrenE (Elem _ _ cs) = map getElem $ filter isElem cs

isElem :: Content i -> Bool
isElem (CElem _ _) = True
isElem _ = False

isString :: Content i -> Bool
isString (CString _ _ _) = True
isString _ = False

getElem :: Content i -> Element i
getElem (CElem elem _) = elem
getElem _ = error "not CElem"

getAttr :: Element i -> String -> Maybe String
getAttr (Elem name attrs conts) n0
	= fmap show $ lookup (N n0) attrs

isElemName :: String -> Element i -> Bool
isElemName n0 (Elem name _ _) = N n0 == name

getElemText :: Element i -> String
getElemText (Elem _ _ cs) = concatMap (\(CString _ s _) -> s) $ filter isString cs
